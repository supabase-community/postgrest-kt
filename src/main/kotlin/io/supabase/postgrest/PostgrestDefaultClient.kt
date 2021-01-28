package io.supabase.postgrest

import io.supabase.postgrest.http.PostgrestHttpClient
import io.supabase.postgrest.http.PostgrestHttpClientApache
import io.supabase.postgrest.json.PostgrestJsonConverter
import io.supabase.postgrest.json.PostgrestJsonConverterJackson
import org.apache.hc.client5.http.impl.classic.HttpClients
import java.net.URI

val jsonConverter = PostgrestJsonConverterJackson()

/**
 * The default client uses Apache HTTP client 5.x and Jackson FasterXML for DTO conversion.
 *
 * If you want to customize, implement [PostgrestHttpClient] and [PostgrestJsonConverter].
 */
class PostgrestDefaultClient(
        uri: URI,
        defaultHeaders: Map<String, String> = emptyMap()
) : PostgrestClient(
        postgrestHttpClient = PostgrestHttpClientApache(
                httpClient = HttpClients.createDefault(),
                postgrestJsonConverter = jsonConverter
        ),
        defaultHeaders = defaultHeaders,
        uri = uri
)