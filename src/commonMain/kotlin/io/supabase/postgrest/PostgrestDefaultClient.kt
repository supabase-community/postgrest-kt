package io.supabase.postgrest

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
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
    httpClient = PostgrestHttpClient(
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
        }
    )
)