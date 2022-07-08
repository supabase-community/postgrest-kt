package io.supabase.postgrest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int,
    val message: String,
    val username: String = "",
    val channel_id: Int
)

@Serializable
data class MessageNoId(
    val message: String,
    val username: String,
    @SerialName("channel_id")
    val channelId: Int
)