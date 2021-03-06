package com.jessecorbett.diskord.api.rest

import com.jessecorbett.diskord.api.model.ChannelType
import com.jessecorbett.diskord.api.model.Overwrite
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChannel(
    @SerialName("name") val name: String,
    @SerialName("type") val type: ChannelType,
    @SerialName("bitrate") val bitrate: Int? = null,
    @SerialName("user_limit") val voiceChannelUserLimit: Int? = null, // Only set if a voice channel
    @SerialName("rate_limit_per_user") val timeBetweenUserMessages: Int? = null, // Does not apply to bots/users with manage permissions,
    @SerialName("position") val position: Int? = null,
    @SerialName("permissions_overwrites") val overwrites: List<Overwrite> = emptyList(),
    @SerialName("parent_id") val categoryId: String? = null,
    @SerialName("nsfw") val nsfw: Boolean = false
)
