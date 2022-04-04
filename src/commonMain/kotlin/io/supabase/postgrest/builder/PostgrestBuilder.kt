package io.supabase.postgrest.builder

import io.ktor.client.utils.*
import io.ktor.http.*
import io.supabase.postgrest.http.PostgrestHttpClient
import kotlinx.serialization.KSerializer

open class PostgrestBuilder<T : Any> {

    constructor(url: Url, schema: String? = null, headers: Headers, httpClient: PostgrestHttpClient) {
        this.url = url
        this.schema = schema
        mBody = body
        this.headers = buildHeaders {
            appendAll(headers)
        }
        this.httpClient = httpClient
    }

    constructor(old: PostgrestBuilder<T>) {
        url = old.url
        mMethod = old.method
        schema = old.schema
        mSearchParams = old.searchParams.toMutableMap()
        mBody = old.body
        headers = old.headers
        httpClient = old.httpClient
    }

    val url: Url?
    val httpClient: PostgrestHttpClient

    private var mMethod: HttpMethod? = null
    val method: HttpMethod?
        get() = mMethod

    val schema: String?
    private var mSearchParams: MutableMap<String, String> = mutableMapOf()
    val searchParams: Map<String, String>
        get() = mSearchParams.toMap()

    private var mBody: Any? = null
    val body: Any?
        get() = mBody

    var headers: Headers = headersOf()
        private set

    internal fun setSearchParam(name: String, value: String) {
        mSearchParams[name] = value
    }

    internal fun setHeader(name: String, value: String) {
        headers = buildHeaders {
            appendAll(headers)
            append(name, value)
        }
    }

    internal fun setMethod(method: HttpMethod) {
        mMethod = method
    }

    protected fun setBody(body: Any?) {
        mBody = body
    }

}

suspend inline fun <reified T : KSerializer<T>> PostgrestBuilder<T>.execute() {
    checkNotNull(method) { "Method cannot be null" }
    checkNotNull(url) { "Url cannot be null" }

    val mHeaders = if (schema != null) {
        buildHeaders {
            appendAll(headers)
            if (method in listOf(HttpMethod.Get, HttpMethod.Head)) {
                append("Accept-Profile", schema)
            } else {
                append(HttpHeaders.ContentType, schema)
            }
        }
    } else {
        headers
    }

    val queryParameters = ParametersBuilder().run {
        searchParams.entries.forEach {
            //value.encodeURLPath()
            append(it.key, it.value)
        }
        build()
    }

    httpClient.execute<T>(
        uri = url.copy(
            parameters = queryParameters
        ),
        method = method!!,
        headers = mHeaders,
        body = body
    )
}
