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
 *
 * @param[uri] URL of the PostgREST endpoint.
 * @param[headers] Custom headers.
 * @param[schema] Postgres schema to switch to.
 */
class PostgrestDefaultClient(
        uri: URI,
        headers: Map<String, String> = emptyMap(),
        schema: String? = null
) : PostgrestClient(
        uri = uri,
        headers = headers,
        schema = schema,
        httpClient = PostgrestHttpClientApache(
                postgrestJsonConverter = jsonConverter,
                httpClient = { HttpClients.createDefault() }
        ),
        jsonConverter = jsonConverter
)