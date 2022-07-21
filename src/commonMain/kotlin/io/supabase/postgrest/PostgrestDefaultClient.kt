package io.supabase.postgrest

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.supabase.postgrest.http.PostgrestHttpClient
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class PostgrestDefaultClient(
    uri: Url,
    headers: Headers = headersOf(),
    schema: String? = null,
) : PostgrestClient(
    url = uri,
    headers = headers,
    schema = schema,
    httpClient = {
        PostgrestHttpClient {
            HttpClient {
                install(Logging) {

                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            explicitNulls = it
                        }
                    )
                }
            }
        }
    }
) {

    constructor(
        uri: Url,
        headersMap: Map<String, String> = mapOf(),
        schema: String? = null,
    ) : this(
        uri,
        Headers.build {
            headersMap.entries.forEach {
                append(it.key, it.value)
            }
        },
        schema
    )

}