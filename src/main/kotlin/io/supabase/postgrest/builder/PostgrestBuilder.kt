package io.supabase.postgrest.builder

import io.supabase.postgrest.http.HttpResponse
import io.supabase.postgrest.http.PostgrestHttpClient
import org.apache.hc.core5.http.Method
import java.net.URI

open class PostgrestBuilder<T : Any> {

    private val postgrestHttpClient: PostgrestHttpClient
    private val url: URI

    private var headers: MutableMap<String, String> = mutableMapOf()
    private var method: Method? = null
    private var schema: String? = null
    private var body: Any? = null
    private var searchParams: MutableMap<String, String> = mutableMapOf()

    constructor(builder: PostgrestBuilder<T>) {
        this.headers = builder.headers
        this.method = builder.method
        this.postgrestHttpClient = builder.postgrestHttpClient
        this.url = builder.url
        this.schema = builder.schema
        this.body = builder.body
    }

    constructor(url: URI, postgrestHttpClient: PostgrestHttpClient, defaultHeaders: Map<String, String>) {
        this.url = url
        this.postgrestHttpClient = postgrestHttpClient

        defaultHeaders.forEach { (name, value) -> setHeader(name, value) }
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

    fun execute(): HttpResponse {
        checkNotNull(method) { "Method cannot be null" }

        val uriParams = searchParams.entries.joinToString("&") { (name,value) -> "$name=$value" }

        val uriWithParams = URI("${this.url}?${uriParams}")

        return postgrestHttpClient.execute(
                url = uriWithParams,
                method = method!!,
                headers = headers,
                body = body,
                schema = schema
        )
    }
}