package io.supabase.postgrest.http

import io.ktor.http.*

data class PostgrestHttpResponse<T>(
    val status: HttpStatusCode,
    val body: T?,
    val count: Long?
)