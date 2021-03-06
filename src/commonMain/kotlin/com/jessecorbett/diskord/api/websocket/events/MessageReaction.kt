package com.jessecorbett.diskord.api.websocket.events

import com.jessecorbett.diskord.api.model.Emoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReaction(
    @SerialName("user_id") val userId: String,
    @SerialName("channel_id") val channelId: String,
    @SerialName("message_id") val messageId: String,
    @SerialName("guild_id") val guildId: String? = null,
    @SerialName("emoji") val emoji: Emoji
)
