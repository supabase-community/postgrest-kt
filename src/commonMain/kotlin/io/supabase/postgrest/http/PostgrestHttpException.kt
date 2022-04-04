package io.supabase.postgrest.http

import io.ktor.http.*

/**
 * Exception is used when a bad status code (> 301) is returned.
 *
 * If you implement your custom PostgrestHttpClient, you need to handle exceptions on your own.
 *
 * @property[status] HTTP status code
 * @property[data] Response body as [String] if available
 */
class PostgrestHttpException(val status: HttpStatusCode, val data: String?, val exception: Exception?) :
    RuntimeException("Unexpected response status: $status")