package com.jessecorbett.diskord.api.gateway

import com.jessecorbett.diskord.api.gateway.model.GatewayMessage
import com.jessecorbett.diskord.internal.websocketClient
import com.jessecorbett.diskord.util.DEBUG_MODE
import com.jessecorbett.diskord.util.defaultJson
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import mu.KotlinLogging

internal class SocketManager(url: String, private val emitMessage: suspend (GatewayMessage) -> Unit) {
    private val logger = KotlinLogging.logger {}

    private var socketClient: HttpClient = buildWSClient()
    private var url = if (url.startsWith("wss://")) {
        url.drop(6)
    } else {
        url
    }

    private var session: WebSocketSession? = null
    private val outgoingMessages: Channel<GatewayMessage> = Channel()
    var running: Boolean = false
        private set

    val alive: Boolean
        get() = running && session != null

    suspend fun open() {
        running = true
        while (running) {
            initializeNewConnection()
        }
    }

    suspend fun close() {
        running = false
        session?.close(CloseReason(WebSocketCloseCode.NORMAL_CLOSURE.code, "This bot requested the connection close"))
        socketClient.close()
    }

    suspend fun restartConnection() {
        session?.close(CloseReason(WebSocketCloseCode.NORMAL_CLOSURE.code, "This bot requested the connection close to restart"))
    }

    suspend fun send(gatewayMessage: GatewayMessage) {
        outgoingMessages.send(gatewayMessage)
    }

    private suspend fun initializeNewConnection() = coroutineScope {
        try {
            socketClient.wss(host = url, port = 443, request = {
                this.url.parameters["v"] = "8"
                this.url.parameters["encoding"] = "json"
                logger.trace { "Building a socket HttpRequest" }
            }) {
                logger.info { "Starting a new websocket connection" }

                session = this

                logger.info { "Starting incoming loop" }

                launch {
                    for (message in outgoingMessages) {
                        send(defaultJson.encodeToString(message))
                    }
                }

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> emitMessage(defaultJson.decodeFromString(GatewayMessage.serializer(), frame.readText()))
                        is Frame.Binary -> TODO("Add support for binary formatted data")
                        is Frame.Close -> logger.info { "Close Frame sent with message: $frame" }
                        is Frame.Ping, is Frame.Pong -> logger.debug { frame }
                    }
                }
                logger.info { "Exited the incoming loop" }

                val closeReason = this@wss.closeReason.await()
                if (closeReason == null) {
                    logger.warn { "Closed with no close reason, probably a connection issue" }
                } else {
                    val closeCode = WebSocketCloseCode.values().find { it.code == closeReason.code }
                    val message = if (closeReason.message.isEmpty()) {
                        "Closed with code '$closeCode' with no reason provided"
                    } else {
                        "Closed with code '$closeCode' with the reason '${closeReason.message}'"
                    }
                    logger.info { message }
                }
            }
        } finally {
            if (running) {
                logger.info { "Socket connection has closed but will restart momentarily" }
            } else {
                logger.info { "Socket connection has closed" }
            }
            session?.cancel()
            session = null
            this.cancel()
        }
    }

    @OptIn(KtorExperimentalAPI::class)
    private fun buildWSClient(): HttpClient {
        return HttpClient(websocketClient()).config {
            install(WebSockets)
            if (DEBUG_MODE) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }
        }
    }
}
