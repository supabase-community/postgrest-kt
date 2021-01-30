package io.supabase.postgrest.http

/**
 * Exception is used when a bad status code (> 301) is returned.
 *
 * If you implement your custom PostgrestHttpClient, you need to handle exceptions on your own.
 *
 * @property[status] HTTP status code
 * @property[data] Response body as [String] if available
 */
class PostgrestHttpException(val status: Int, val data: String?) : RuntimeException("Unexpected response status: $status")