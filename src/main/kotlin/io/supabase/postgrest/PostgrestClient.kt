package io.supabase.postgrest

import io.supabase.postgrest.builder.PostgrestBuilder
import io.supabase.postgrest.builder.PostgrestQueryBuilder
import io.supabase.postgrest.http.PostgrestHttpClient
import io.supabase.postgrest.json.PostgrestJsonConverter
import java.net.URI

/**
 * Main client and entry point for using PostgresREST client.
 *
 * @param[uri] URL of the PostgresREST endpoint.
 * @param[headers] Custom headers.
 * @param[schema] Postgres schema to switch to.
 * @param[httpClient] Implementation of the [PostgrestHttpClient] interface.
 * @param[jsonConverter] Implementation of the [PostgrestJsonConverter] interface
 */
open class PostgrestClient(
        private val uri: URI,
        private val headers: Map<String, String> = emptyMap(),
        private val schema: String? = null,
        private val httpClient: PostgrestHttpClient,
        private val jsonConverter: PostgrestJsonConverter
) {

    /**
     * Perform a table operation.
     *
     * @param[table] The table name to operate on.
     */
    fun <T : Any> from(table: String): PostgrestQueryBuilder<T> {
        val uri = URI("$uri/$table")
        return PostgrestQueryBuilder(uri, httpClient, jsonConverter, headers, schema)
    }

    /**
     * Perform a stored procedure call.
     *
     * @param[fn] The function name to call.
     * @param[params] The parameters to pass to the function call.
     */
    fun <T : Any> rpc(fn: String, params: Any?): PostgrestBuilder<T> {
        val uri = URI("${this.uri}/rpc/${fn}")

        return PostgrestQueryBuilder<T>(uri, httpClient, jsonConverter, headers, schema).rpc(params)
    }

}
