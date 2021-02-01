package io.supabase.postgrest.builder

import io.supabase.postgrest.http.PostgrestHttpClient
import io.supabase.postgrest.http.PostgrestHttpResponse
import io.supabase.postgrest.json.PostgrestJsonConverter
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.http.Method
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

open class PostgrestBuilder<T : Any> {

    val httpClient: PostgrestHttpClient
    val jsonConverter: PostgrestJsonConverter
    private val url: URI

    private var schema: String? = null
    private var headers: MutableMap<String, String> = mutableMapOf()
    private var method: Method? = null
    private var body: Any? = null
    private var searchParams: MutableMap<String, String> = mutableMapOf()

    constructor(builder: PostgrestBuilder<T>) {
        this.headers = builder.headers
        this.method = builder.method
        this.httpClient = builder.httpClient
        this.url = builder.url
        this.body = builder.body
        this.jsonConverter = builder.jsonConverter
        this.schema = schema
    }

    constructor(url: URI, httpClient: PostgrestHttpClient, jsonConverter: PostgrestJsonConverter, headers: Map<String, String>, schema: String?) {
        this.url = url
        this.httpClient = httpClient
        this.jsonConverter = jsonConverter
        this.schema = schema

        headers.forEach { (name, value) -> setHeader(name, value) }
    }

    protected fun setHeader(name: String, value: String) {
        this.headers[name] = value
    }

    protected fun setSearchParam(name: String, value: String) {
        this.searchParams[name] = value
    }

    protected fun setMethod(method: Method) {
        this.method = method
    }

    protected fun setBody(body: Any?) {
        this.body = body
    }

    fun getSearchParams(): Map<String, String> {
        return searchParams
    }

    fun execute(): PostgrestHttpResponse {
        checkNotNull(method) { "Method cannot be null" }

        // https://postgrest.org/en/stable/api.html#switching-schemas
        if (schema != null) {
            // skip
            if (this.method in listOf(Method.GET, Method.HEAD)) {
                setHeader("Accept-Profile", this.schema!!)
            } else {
                setHeader(HttpHeaders.CONTENT_TYPE, this.schema!!)
            }
        }

        if (this.method != Method.GET && this.method != Method.HEAD) {
            setHeader(HttpHeaders.CONTENT_TYPE,  ContentType.APPLICATION_JSON.mimeType)
        }

        val uriParams = searchParams.entries.joinToString("&") { (name, value) -> "$name=${URLEncoder.encode(value, StandardCharsets.UTF_8.name())}" }

        val uriWithParams = URI("${this.url}?${uriParams}")

        return httpClient.execute(
                uri = uriWithParams,
                method = method!!,
                headers = headers,
                body = body
        )
    }

    inline fun <reified R : Any> executeAndGetSingle(): R {
        val response = execute()

        return jsonConverter.deserialize(response.body!!, R::class.java)
    }

    inline fun <reified R : Any> executeAndGetList(): List<R> {
        val response = execute()

        return jsonConverter.deserializeList(response.body!!, R::class.java)
    }
}