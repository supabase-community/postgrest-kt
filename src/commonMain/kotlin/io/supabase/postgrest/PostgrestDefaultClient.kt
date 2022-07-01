package io.supabase.postgrest

import io.ktor.client.*
import io.ktor.client.engine.*
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
    clientEngine: HttpClientEngine,
    json: Json = Json {
//        serializersModule = SerializersModule {
//            contextual(String.serializer())
//            contextual(Int.serializer())
//            contextual(Float.serializer())
//            contextual(Double.serializer())
//            contextual(Boolean.serializer())
//            contextual(Char.serializer())
//            contextual(Byte.serializer())
//            contextual(PolymorphicSerializer(Map::class))
//
//        }
        this.allowStructuredMapKeys
        ignoreUnknownKeys = true
    }
) : PostgrestClient(
    url = uri,
    headers = headers,
    schema = schema,
    httpClient = PostgrestHttpClient(
        HttpClient(clientEngine) {
            install(Logging) {

            }

            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
        }
    )
)