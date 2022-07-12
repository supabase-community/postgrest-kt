package io.supabase.postgrest

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.supabase.postgrest.http.PostgrestHttpClient
import kotlinx.serialization.json.Json

class PostgrestDefaultClient(
    uri: Url,
    headers: Headers = headersOf(),
    schema: String? = null,
    json: Json = Json {
        ignoreUnknownKeys = true
    }
) : PostgrestClient(
    url = uri,
    headers = headers,
    schema = schema,
    httpClient = {
        PostgrestHttpClient(
            HttpClient {
                install(Logging) {

                }

                install(ContentNegotiation) {
                    json(json)
                }
            }
        )
    }
) {

    constructor(
        uri: Url,
        headersMap: Map<String, String> = mapOf(),
        schema: String? = null,
        json: Json = Json {
            ignoreUnknownKeys = true
        }
    ) : this(
        uri,
        Headers.build {
            headersMap.entries.forEach {
                append(it.key, it.value)
            }
        },
        schema,
        json
    )

}