package io.supabase.postgrest.http

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.core.*

class PostgrestHttpClient(val httpClient: HttpClient) {

    suspend inline fun <reified T> execute(
        uri: Url,
        method: HttpMethod,
        headers: Map<String, List<String>> = emptyMap(),
        body: Any? = null
    ): Result<PostgrestHttpResponse<T>> {
        val result = runCatching {
            httpClient.use { httpClient ->
                httpClient.request<HttpResponse>(uri) {
                    this.method = method
                    if (body != null) {
                        this.body = body
                    }

                    buildHeaders {
                        headers.forEach {
                            appendAll(it.key, it.value)
                        }
                    }
                }
            }
        }

        if (result.isFailure) {
            return when (val ex = result.exceptionOrNull()) {
                is RedirectResponseException -> {
                    Result.failure(PostgrestHttpException(ex.response.status, ex.response.readText()))
                }

                is Exception -> {
                    Result.failure(
                        PostgrestHttpException(
                            HttpStatusCode(418, "I'm a teapot"),
                            ex.stackTraceToString()
                        )
                    )
                }

                else -> Result.failure(
                    PostgrestHttpException(
                        HttpStatusCode(418, "I'm a teapot"),
                        "Null Exception"
                    )
                )
            }
        }

        return Result.success(responseHandler(result.getOrThrow()))
    }

}

suspend inline fun <reified T> responseHandler(response: HttpResponse): PostgrestHttpResponse<T> {
//    val statusSuccessful =
//        response.status.isSuccess() || response.status == HttpStatusCode.TemporaryRedirect || response.status == HttpStatusCode.PermanentRedirect
//
//    if (!statusSuccessful) {
//        val entityAsString = response.receive<String>()
//
//        throw PostgrestHttpException(response.status, entityAsString)
//    }

    val count = extractCount(response.headers.toMap(), response.request.headers.toMap())
    val obj = response.receive<T>()

    return PostgrestHttpResponse(
        status = response.status,
        body = obj,
        count = count
    )
}