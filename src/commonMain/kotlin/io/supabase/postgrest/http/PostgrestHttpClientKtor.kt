package io.supabase.postgrest.http

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlin.coroutines.cancellation.CancellationException

class PostgrestHttpClient(val httpClient: (serializeNull: Boolean) -> HttpClient) {

    suspend inline fun <reified T> execute(
        uri: Url,
        method: HttpMethod,
        headers: Headers = headersOf(),
        body: Any? = null,
        serializeNull: Boolean = true
    ): Result<PostgrestHttpResponse<T>> {
        try {
            val callResult = httpClient(serializeNull).use { httpClient ->
                httpClient.request(uri) {
                    this.method = method
                    if (body != null) {
                        setBody(body)
                    }

                    headers {
                        appendAll(headers)
                    }
                }
            }
            val resultProcessed = responseHandler<T>(callResult)

            return Result.success(resultProcessed)
        } catch (ex: Exception) {
            Logger.e("Error Call", ex)
            return when (ex) {
                is ClientRequestException -> {
                    Result.failure(
                        PostgrestHttpException(ex.response.status, ex.response.bodyAsText(), ex)
                    )
                }

                is ResponseException -> {
                    Result.failure(
                        PostgrestHttpException(ex.response.status, ex.response.bodyAsText(), ex)
                    )
                }

                is PostgrestHttpException -> {
                    Result.failure(ex)
                }

                else -> {
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

    @Throws(PostgrestHttpException::class, CancellationException::class)
    suspend inline fun <reified T> responseHandler(response: HttpResponse): PostgrestHttpResponse<T> {
        val statusSuccessful = response.status.isSuccess()

        if (!statusSuccessful) {
            val entityAsString = response.body<String>()

            throw PostgrestHttpException(response.status, entityAsString, null)
        }

        val count = extractCount(response.headers.toMap(), response.request.headers.toMap())

        val obj = if (T::class == String::class) {
            response.bodyAsText()
        } else {
            response.body<T>()
        } as T

        return PostgrestHttpResponse(
            status = response.status,
            body = obj,
            count = count
        )
    }
}