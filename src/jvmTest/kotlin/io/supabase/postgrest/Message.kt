package io.supabase.postgrest

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int,
    val message: String,
    val channel_id: Int
)