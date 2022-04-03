package io.supabase.postgrest.builder

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.supabase.postgrest.http.PostgrestHttpClient
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

open class PostgrestBuilder<T : Any> {

    constructor(builder: Builder<T>) {
        jsonConverter = builder.jsonConverter
        url = builder.url
        schema = builder.schema
//        mSearchParams = builder.searchParams
        mBody = builder.body
        builder.headers.forEach {
            setHeader(it.key, it.value)
        }
    }

    constructor(old: PostgrestBuilder<T>) {
        jsonConverter = old.jsonConverter
        url = old.url
        mMethod = old.method
        schema = old.schema
        mSearchParams = old.searchParams.toMutableMap()
        mBody = old.body
        mHeaders = old.mHeaders
    }

    val jsonConverter: Json
    val url: Url?

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

    private var mHeaders: MutableMap<String, List<String>> = mutableMapOf()
    val headers: Map<String, List<String>>
        get() = mHeaders

    internal fun setSearchParam(name: String, value: String) {
        mSearchParams[name] = value
    }

    internal fun setHeader(name: String, value: String) {
        mHeaders[name] = listOf(value)
    }

    internal fun setMethod(method: HttpMethod) {
        mMethod = method
    }

    protected fun setBody(body: Any?) {
        mBody = body
    }

    open class Builder<T : Any> {
        var jsonConverter: Json = Json {
            ignoreUnknownKeys = true
        }
        var url: Url? = null
        var schema: String? = null
        var headers: MutableMap<String, String> = mutableMapOf()
        var body: T? = null
        open fun build() = PostgrestBuilder(this)
    }

    companion object {
        fun <T : Any> postrest(block: PostgrestBuilder.Builder<T>.() -> Unit) = Builder<T>().apply(block).build()

        fun <T : Any> postrest(old: PostgrestBuilder<T>) = PostgrestBuilder(old)
    }
}

suspend inline fun <reified T : KSerializer<T>> PostgrestBuilder<T>.execute() {
    checkNotNull(method) { "Method cannot be null" }
    checkNotNull(url) { "Url cannot be null" }

    val mHeaders = headers.toMutableMap()

    if (schema != null) {
        if (method in listOf(HttpMethod.Get, HttpMethod.Head)) {
            mHeaders["Accept-Profile"] = listOf(schema)
        } else {
            mHeaders[HttpHeaders.ContentType] = listOf(schema)
        }
    }

    val queryParameters = ParametersBuilder().run {
        searchParams.entries.forEach {
            //value.encodeURLPath()
            append(it.key, it.value)
        }
        build()
    }

    //TODO MOVE THIS "GLOBALLY"
    val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(jsonConverter)
        }
    }

    PostgrestHttpClient(httpClient)
        .execute<T>(
            uri = url.copy(
                parameters = queryParameters
            ),
            method = method!!,
            headers = mHeaders,
            body = body
        )
}
