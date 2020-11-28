package com.jessecorbett.diskord.api.gateway.commands

import com.jessecorbett.diskord.api.gateway.model.GatewayIntents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentifyShard(
    @SerialName("token") val token: String,
    @SerialName("shard") val shard: List<Int>,
    @SerialName("compress") val canCompress: Boolean = false,
    @SerialName("large_threshold") val memberCountThreshold: Int = 50,
    @SerialName("presence") val presence: UpdateStatus? = null,
    @SerialName("properties") val properties: IdentifyProperties,
    @SerialName("intents") val intents: GatewayIntents? = null
)

@Serializable
data class Identify(
    @SerialName("token") val token: String,
    @SerialName("compress") val canCompress: Boolean = false,
    @SerialName("large_threshold") val memberCountThreshold: Int = 50,
    @SerialName("presence") val presence: UpdateStatus? = null,
    @SerialName("properties") val properties: IdentifyProperties,
    @SerialName("intents") val intents: GatewayIntents? = null
)

@Serializable
data class IdentifyProperties(
    @SerialName("\$os") val os: String,
    @SerialName("\$browser") val browser: String,
    @SerialName("\$device") val device: String
) {
    companion object {
        val Default = IdentifyProperties("JVM", "diskord", "diskord")
    }
}
