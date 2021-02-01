package io.supabase.postgrest.http

import org.apache.hc.core5.http.Method
import java.net.URI

/**
 * Interface used by the PostgrestClient, allows replacing the default HTTP client.
 *
 * Overwrite it to replace the default Apache HTTP Client implementation.
 */
interface PostgrestHttpClient {

    fun execute(
            uri: URI,
            method: Method,
            headers: Map<String, String> = emptyMap(),
            body: Any? = null
    ): PostgrestHttpResponse
}

