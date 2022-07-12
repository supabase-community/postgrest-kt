package io.supabase.postgrest.builder

import io.ktor.http.*
import io.supabase.postgrest.http.PostgrestHttpClient

class PostgrestQueryBuilder<T : Any> : PostgrestBuilder<T> {

    constructor(url: Url, schema: String? = null, headers: Headers, httpClient: PostgrestHttpClient)
            : super(url, schema, headers, httpClient)

    internal constructor(builder: PostgrestBuilder<T>) : super(builder)

    companion object {
        const val HEADER_PREFER = "Prefer"
    }

    /**
     * Performs vertical filtering with SELECT.
     *
     * @param[columns] The columns to retrieve, separated by commas.
     * @param[head] When set to true, select will void data.
     * @param[count] Count algorithm to use to count rows in a table.
     */
    fun select(
        columns: String = "*",
        head: Boolean = false,
        count: Count? = null
    ): PostgrestFilterBuilder<T> {
        if (head) {
            setMethod(HttpMethod.Head)
        } else {
            setMethod(HttpMethod.Get)
        }

        val cleanedColumns = cleanColumns(columns)

        setSearchParam("select", cleanedColumns)

        if (count != null) {
            setHeader(HEADER_PREFER, "count=${count.identifier}")
        }

        return PostgrestFilterBuilder(this)
    }

    /**
     * Performs an INSERT into the table.
     *
     * @param[values] The values to insert.
     * @param[upsert] If `true`, performs an UPSERT.
     * @param[onConflict] By specifying the `on_conflict` query parameter, you can make UPSERT work on a column(s) that has a UNIQUE constraint.
     * @param[returning] By default the new record is returned. Set this to 'minimal' if you don't need this value.
     */
    fun insert(
        values: List<T>,
        upsert: Boolean = false,
        onConflict: String? = null,
        returning: Returning = Returning.REPRESENTATION,
        count: Count? = null
    ): PostgrestFilterBuilder<T> {
        setMethod(HttpMethod.Post)

        val preferHeaders = mutableListOf("return=${returning.identifier}")
        if (upsert) preferHeaders.add("resolution=merge-duplicates")

        if (upsert && onConflict != null) setSearchParam("on_conflict", onConflict)
        setBody(values)

        if (count != null) {
            preferHeaders.add("count=${count.identifier}")
        }

        setHeader(HEADER_PREFER, preferHeaders.joinToString(","))

        return PostgrestFilterBuilder(this)
    }

    /**
     * Performs an INSERT into the table.
     *
     * @param[value] The value to insert.
     * @param[upsert] If `true`, performs an UPSERT.
     * @param[onConflict] By specifying the `on_conflict` query parameter, you can make UPSERT work on a column(s) that has a UNIQUE constraint.
     * @param[returning] By default the new record is returned. Set this to 'minimal' if you don't need this value.
     */
    fun insert(
        value: T,
        upsert: Boolean = false,
        onConflict: String? = null,
        returning: Returning = Returning.REPRESENTATION,
        count: Count? = null
    ): PostgrestFilterBuilder<T> {
        return insert(listOf(value), upsert, onConflict, returning, count)
    }

    /**
     * Performs an UPDATE on the table.
     *
     * @param[value] The values to update.
     * @param[returning] By default the updated record is returned. Set this to 'minimal' if you don't need this value.
     */
    fun update(
        value: T,
        returning: Returning = Returning.REPRESENTATION,
        count: Count? = null
    ): PostgrestFilterBuilder<T> {
        setMethod(HttpMethod.Patch)
        setBody(value)

        val prefersHeaders = mutableListOf("return=${returning.identifier}")

        if (count != null) {
            prefersHeaders.add("count=${count.identifier}")
        }
        setHeader(HEADER_PREFER, prefersHeaders.joinToString(","))

        return PostgrestFilterBuilder(this)
    }

    /**
     * Performs a DELETE on the table.
     *
     * @param[returning] If `true`, return the deleted row(s) in the response.
     */
    fun delete(returning: Returning = Returning.REPRESENTATION, count: Count? = null): PostgrestFilterBuilder<T> {
        setMethod(HttpMethod.Delete)

        val prefersHeaders = mutableListOf("return=${returning.identifier}")
        if (count != null) {
            prefersHeaders.add("count=${count.identifier}")
        }
        setHeader(HEADER_PREFER, prefersHeaders.joinToString(","))

        return PostgrestFilterBuilder(this)
    }

    internal fun rpc(params: Any?): PostgrestBuilder<T> {
        setMethod(HttpMethod.Post)
        setBody(params)
        return this
    }
}

enum class Count(val identifier: String) {
    EXACT("exact"),
    PLANNED("planned"),
    ESTIMATED("estimated")
}

enum class Returning(val identifier: String) {
    MINIMAL("minimal"),
    REPRESENTATION("representation")
}