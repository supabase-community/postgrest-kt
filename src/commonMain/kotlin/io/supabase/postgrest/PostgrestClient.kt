package io.supabase.postgrest

import io.ktor.http.*
import io.supabase.postgrest.builder.PostgrestBuilder
import io.supabase.postgrest.builder.PostgrestQueryBuilder
import io.supabase.postgrest.http.PostgrestHttpClient

open class PostgrestClient(
    private val url: Url,
    private val httpClient: () -> PostgrestHttpClient,
    private val headers: Headers,
    private val schema: String? = null
) {

    /**
     * Perform a table operation.
     *
     * @param[table] The table name to operate on.
     */
    fun <T : Any> from(table: String): PostgrestQueryBuilder<T> {
        val uri = URLBuilder(url).run {
            path(table)
            build()
        }

        return PostgrestQueryBuilder(
            url = uri,
            httpClient = httpClient(),
            headers = headers,
            schema = schema
        )
    }

    /**
     * Perform a stored procedure call.
     *
     * @param[fn] The function name to call.
     * @param[params] The parameters to pass to the function call.
     */
    fun <T : Any> rpc(fn: String, params: Any?): PostgrestBuilder<T> {
        val uri = URLBuilder(url).run {
            path("/rpc/$fn")
            build()
        }

        return PostgrestQueryBuilder<T>(
            url = uri,
            httpClient = httpClient(),
            headers = headers,
            schema = schema
        ).rpc(params)
    }
}