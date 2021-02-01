package io.supabase.postgrest.http

data class PostgrestHttpResponse(
        val status: Int,
        val body: String?,
        val count: Long?
)