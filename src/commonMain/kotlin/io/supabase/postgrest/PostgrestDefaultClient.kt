package io.supabase.postgrest

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
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

                install(JsonFeature) {
                    serializer = KotlinxSerializer(json)
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