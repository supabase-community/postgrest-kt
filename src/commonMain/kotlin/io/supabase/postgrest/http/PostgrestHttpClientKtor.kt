package io.supabase.postgrest.http

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlin.coroutines.cancellation.CancellationException

class PostgrestHttpClient(val httpClient: HttpClient) {

    suspend inline fun <reified T> execute(
        uri: Url,
        method: HttpMethod,
        headers: Headers = headersOf(),
        body: Any? = null
    ): Result<PostgrestHttpResponse<T>> {
        try {
            val callResult = httpClient.use { httpClient ->
                httpClient.request<HttpResponse>(uri) {
                    this.method = method
                    if (body != null) {
                        this.body = body
                    }
//                    if (body != null) {
//                        this.body = when (body) {
//                            is Map<*, *> -> {
//                                body.toJsonElement()
//                            }
//
//                            is List<*> -> {
//                                body.toJsonElement()
//                            }
//                            else -> {
//                                body
//                            }
//
//                        }
//
//                    }

                    headers {
                        appendAll(headers)
                    }
                }
            }
            val resultProcessed = responseHandler<T>(callResult)

            return Result.success(resultProcessed)
        } catch (ex: Exception) {
            return when (ex) {
                is ClientRequestException -> {
                    Result.failure(
                        PostgrestHttpException(ex.response.status, ex.response.readText(), ex)
                    )
                }

                is ResponseException -> {
                    Result.failure(
                        PostgrestHttpException(ex.response.status, ex.response.readText(), ex)
                    )
                }

                is PostgrestHttpException -> {
                    Result.failure(ex)
                }

                else -> {
                    Logger.e("Error Call", ex)

                    Result.failure(
                        PostgrestHttpException(
                            HttpStatusCode(418, "I'm a teapot"),
                            ex.message,
                            ex
                        )
                    )
                }
            }
        }
    }

//    fun List<*>.toJsonElement(): JsonElement {
//        val list: MutableList<JsonElement> = mutableListOf()
//        this.forEach { value ->
//            when (value) {
//                null -> list.add(JsonNull)
//                is Map<*, *> -> list.add(value.toJsonElement())
//                is List<*> -> list.add(value.toJsonElement())
//                is Boolean -> list.add(JsonPrimitive(value))
//                is Number -> list.add(JsonPrimitive(value))
//                is String -> list.add(JsonPrimitive(value))
//                is Enum<*> -> list.add(JsonPrimitive(value.toString()))
//                else -> throw IllegalStateException("Can't serialize unknown collection type: $value")
//            }
//        }
//        return JsonArray(list)
//    }
//
//    fun Map<*, *>.toJsonElement(): JsonElement {
//        val map: MutableMap<String, JsonElement> = mutableMapOf()
//        this.forEach { (key, value) ->
//            key as String
//            when (value) {
//                null -> map[key] = JsonNull
//                is Map<*, *> -> map[key] = value.toJsonElement()
//                is List<*> -> map[key] = value.toJsonElement()
//                is Boolean -> map[key] = JsonPrimitive(value)
//                is Number -> map[key] = JsonPrimitive(value)
//                is String -> map[key] = JsonPrimitive(value)
//                is Enum<*> -> map[key] = JsonPrimitive(value.toString())
//                else -> throw IllegalStateException("Can't serialize unknown type: $value")
//            }
//        }
//        return JsonObject(map)
//    }

    @Throws(PostgrestHttpException::class, CancellationException::class)
    suspend inline fun <reified T> responseHandler(response: HttpResponse): PostgrestHttpResponse<T> {
        val statusSuccessful = response.status.isSuccess()

        if (!statusSuccessful) {
            val entityAsString = response.receive<String>()

            throw PostgrestHttpException(response.status, entityAsString, null)
        }

        val count = extractCount(response.headers.toMap(), response.request.headers.toMap())

        val obj = if (T::class == String::class) {
            response.readText()
        } else {
            response.receive<T>()
        } as T

        return PostgrestHttpResponse(
            status = response.status,
            body = obj,
            count = count
        )
    }
}