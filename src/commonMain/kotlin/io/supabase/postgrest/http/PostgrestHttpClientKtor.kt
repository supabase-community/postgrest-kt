package io.supabase.postgrest.http

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer

class PostgrestHttpClient(val httpClient: HttpClient) {

    suspend inline fun <reified T : KSerializer<T>> execute(
        uri: Url,
        method: HttpMethod,
        headers: Map<String, List<String>>,
        body: Any?
    ): Result<PostgrestHttpResponse<T>> {
        val response = httpClient.use { httpClient ->
            httpClient.request<HttpResponse>(uri) {
                this.method = method
                if (body != null) {
                    this.body = body
                }
                this.headers.build()
                headers.forEach {
                    this.headers.appendAll(it.key, it.value)
                }
            }
        }

        val responseObj = responseHandler<T>(response)

        return Result.success(responseObj)
    }

}

suspend inline fun <reified T : KSerializer<T>> responseHandler(response: HttpResponse): PostgrestHttpResponse<T> {
    val statusSuccessful =
        response.status.isSuccess() || response.status == HttpStatusCode.TemporaryRedirect || response.status == HttpStatusCode.PermanentRedirect

    if (!statusSuccessful) {
        val entityAsString = response.receive<String>()

        throw PostgrestHttpException(response.status, entityAsString)
    }

    val count = extractCount(response.headers.toMap(), response.request.headers.toMap())
    val obj = response.receive<T>()

    return PostgrestHttpResponse(
        status = response.status,
        body = obj,
        count = count
    )
}