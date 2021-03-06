package com.jessecorbett.diskord.api.websocket.model

import com.jessecorbett.diskord.api.websocket.events.DiscordEvent
import kotlinx.serialization.*

/**
 * An enum defining all currently supported gateway intents.
 */
enum class GatewayIntent(val mask: Int, val privileged: Boolean = false) {
    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.GUILD_CREATE]
     * - [DiscordEvent.GUILD_UPDATE]
     * - [DiscordEvent.GUILD_DELETE]
     * - [DiscordEvent.GUILD_ROLE_CREATE]
     * - [DiscordEvent.GUILD_ROLE_UPDATE]
     * - [DiscordEvent.GUILD_ROLE_DELETE]
     * - [DiscordEvent.CHANNEL_CREATE]
     * - [DiscordEvent.CHANNEL_UPDATE]
     * - [DiscordEvent.CHANNEL_DELETE]
     * - [DiscordEvent.CHANNEL_PINS_UPDATE]
     */
    GUILDS(0x00000001),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.GUILD_MEMBER_ADD]
     * - [DiscordEvent.GUILD_MEMBER_UPDATE]
     * - [DiscordEvent.GUILD_MEMBER_REMOVE]
     *
     * Note: This intent is privileged and may require verification with Discord
     * (see [https://support.discord.com/hc/en-us/articles/360040720412-Bot-Verification-and-Data-Whitelisting]).*
     */
    GUILD_MEMBERS(0x00000002, true),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.GUILD_BAN_ADD]
     * - [DiscordEvent.GUILD_BAN_REMOVE]
     */
    GUILD_BANS(0x00000004),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.GUILD_EMOJIS_UPDATE]
     */
    GUILD_EMOJIS(0x00000008),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.GUILD_INTEGRATIONS_UPDATE]
     */
    GUILD_INTEGRATIONS(0x00000010),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.WEBHOOKS_UPDATE]
     */
    GUILD_WEBHOOKS(0x00000020),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.INVITE_CREATE]
     * - [DiscordEvent.INVITE_DELETE]
     */
    GUILD_INVITES(0x00000040),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.VOICE_STATE_UPDATE]
     */
    GUILD_VOICE_STATES(0x00000080),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.PRESENCE_UPDATE]
     *
     * *Note: This intent is privileged and may require verification with Discord
     * (see [https://support.discord.com/hc/en-us/articles/360040720412-Bot-Verification-and-Data-Whitelisting]).*
     */
    GUILD_PRESENCES(0x00000100, true),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.MESSAGE_CREATE]
     * - [DiscordEvent.MESSAGE_UPDATE]
     * - [DiscordEvent.MESSAGE_DELETE]
     * - [DiscordEvent.MESSAGE_DELETE_BULK]
     */
    GUILD_MESSAGES(0x00000200),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.MESSAGE_REACTION_ADD]
     * - [DiscordEvent.MESSAGE_REACTION_REMOVE]
     * - [DiscordEvent.MESSAGE_REACTION_REMOVE_ALL]
     * - [DiscordEvent.MESSAGE_REACTION_REMOVE_EMOJI]
     */
    GUILD_MESSAGE_REACTIONS(0x00000400),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.TYPING_START] (Guilds)
     */
    GUILD_MESSAGE_TYPING(0x00000800),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.MESSAGE_CREATE]
     * - [DiscordEvent.MESSAGE_UPDATE]
     * - [DiscordEvent.MESSAGE_DELETE]
     * - [DiscordEvent.CHANNEL_PINS_UPDATE]
     */
    DIRECT_MESSAGES(0x00001000),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.MESSAGE_REACTION_ADD]
     * - [DiscordEvent.MESSAGE_REACTION_REMOVE]
     * - [DiscordEvent.MESSAGE_REACTION_REMOVE_ALL]
     * - [DiscordEvent.MESSAGE_REACTION_REMOVE_EMOJI]
     */
    DIRECT_MESSAGE_REACTIONS(0x00002000),

    /**
     * Subscribe to the following events:
     *
     * - [DiscordEvent.TYPING_START] (DMs)
     */
    DIRECT_MESSAGE_TYPING(0x00004000)
}

/**
 * An immutable collection of [GatewayIntent] values stored as a bitmask integer.  This is intended for use when
 * creating a Diskord [Bot] instance to signal to the Discord API requested intents.
 *
 * Example:
 * ```kotlin
 *     bot(apiKey, intents = GatewayIntents.of(GatewayIntents.GUILDS, GatewayIntents.GUILD_MEMBERS)) {
 *         guildUpdated {
 *             // do something
 *         }
 *         userJoinedGuild {
 *             // do something
 *         }
 *     }
 * ```
 *
 * *Note: All operations on [GatewayIntents] instances will create a new [GatewayIntents] instance.*
 */
@Serializable(with = GatewayIntentsSerializer::class)
data class GatewayIntents(val value: Int) {
    operator fun contains(intent: GatewayIntent): Boolean {
        return intent in value
    }

    operator fun contains(permissions: GatewayIntents): Boolean {
        return value and permissions.value == permissions.value
    }

    operator fun plus(intents: Int) = GatewayIntents(value or intents)

    operator fun plus(intents: GatewayIntents) = plus(intents.value)

    operator fun plus(intents: Collection<GatewayIntent>) = intents.forEach { plus(it.mask) }

    operator fun plus(intent: GatewayIntent) = plus(intent.mask)

    operator fun minus(intents: Int) = GatewayIntents(value and intents.inv())

    operator fun minus(intents: GatewayIntents) = minus(intents.value)

    operator fun minus(intents: Collection<GatewayIntent>) = intents.forEach { minus(it.mask) }

    operator fun minus(intent: GatewayIntent) = minus(intent.mask)

    override fun toString() = "GatewayIntents($value) --> ${GatewayIntent.values().filter { it in value }.joinToString()}"

    companion object {
        /**
         * A [GatewayIntents] object specifying all intents including privileged intents.
         */
        val ALL = of(*GatewayIntent.values())

        /**
         * A [GatewayIntents] object specifying all intents excluding privileged intents.
         */
        val NON_PRIVILEGED = of(GatewayIntent.values().filterNot { it.privileged })

        /**
         * A [GatewayIntents] object specifying no intents.
         */
        val NONE = GatewayIntents(0)

        /**
         * Create a [GatewayIntents] object including all intents specified in the provided collection.
         */
        fun of(intents: Collection<GatewayIntent>) =
            GatewayIntents(intents.map { intent -> intent.mask }.reduce { left, right -> left or right })

        /**
         * Create a [GatewayIntents] object including all intents specified.
         */
        fun of(vararg intents: GatewayIntent) =
            GatewayIntents(intents.map { intent -> intent.mask }.reduce { left, right -> left or right })

        private operator fun Int.contains(intent: GatewayIntent) = this and intent.mask == intent.mask
    }
}

object GatewayIntentsSerializer : KSerializer<GatewayIntents> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("GatewayIntents", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder) = GatewayIntents(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: GatewayIntents) = if (value.value > 0) {
        encoder.encodeInt(value.value)
    } else {
        encoder.encodeNull()
    }
}
