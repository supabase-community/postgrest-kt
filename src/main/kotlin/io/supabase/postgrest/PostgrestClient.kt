package io.supabase.postgrest

import io.supabase.postgrest.builder.PostgrestBuilder
import io.supabase.postgrest.builder.PostgrestQueryBuilder
import io.supabase.postgrest.http.PostgrestHttpClient
import java.net.URI

open class PostgrestClient(
        private val uri: URI,
        private val postgrestHttpClient: PostgrestHttpClient,
        private val defaultHeaders: Map<String, String> = emptyMap()
) {

    /**
     * Perform a table operation.
     *
     * @param[table] The table name to operate on.
     */
    fun <T : Any> from(table: String): PostgrestQueryBuilder<T> {
        val uri = URI("$uri/$table")
        return PostgrestQueryBuilder(uri, postgrestHttpClient, defaultHeaders)
    }

    fun <T : Any> rpc(fn: String, params: Any?): PostgrestBuilder<T> {
        val uri = URI("${this.uri}/rpc/${fn}")

        return PostgrestQueryBuilder<T>(uri, postgrestHttpClient, defaultHeaders).rpc(params)
    }

}
