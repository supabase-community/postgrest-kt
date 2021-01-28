package io.supabase.postgrest.builder

import io.supabase.postgrest.http.PostgrestHttpClient
import org.apache.hc.core5.http.Method
import java.net.URI

class PostgrestQueryBuilder<T : Any>(url: URI, postgrestHttpClient: PostgrestHttpClient, defaultHeaders: Map<String, String>) : PostgrestBuilder<T>(url, postgrestHttpClient, defaultHeaders) {

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
        setMethod(Method.GET)

        val cleanedColumns = cleanColumns(columns)

        setSearchParam("select", cleanedColumns)

        if (count != null) {
            setHeader("Prefer", "count=${count.identifier}")
        }

        if (head) {
            setMethod(Method.HEAD)
        }

        return PostgrestFilterBuilder(this)
    }

    fun insert(value: List<T>, upsert: Boolean = false, onConflict: String? = null, returning: Returning = Returning.REPRESENTATION, count: Count?): PostgrestFilterBuilder<T> {
        setMethod(Method.POST)

        val preferHeaders = mutableListOf<String>("return=${returning.identifier}")
        if (upsert) preferHeaders.add("resolution=merge-duplicates")

        if (upsert && onConflict != null) setSearchParam("on_conflict", onConflict)
        setBody(value)

        if (count != null) {
            preferHeaders.add("count=${count}")
        }

        setHeader("Prefer", preferHeaders.joinToString(","))

        return PostgrestFilterBuilder(this)
    }

    fun insert(value: T, upsert: Boolean = false, onConflict: String? = null, returning: Returning = Returning.REPRESENTATION, count: Count?): PostgrestFilterBuilder<T> {
        return insert(listOf(value), upsert, onConflict, returning, count)
    }

    fun update(value: Any, returning: Returning = Returning.REPRESENTATION, count: Count?): PostgrestFilterBuilder<T> {
        setMethod(Method.PATCH)
        val prefersHeaders = mutableListOf("return=${returning.identifier}")
        setBody(value)
        if (count != null) {
            prefersHeaders.add("count=${count}")
        }
        setHeader("Prefer", prefersHeaders.joinToString(","))

        return PostgrestFilterBuilder(this)
    }

    fun delete(returning: Returning = Returning.REPRESENTATION, count: Count?): PostgrestFilterBuilder<T> {
        setMethod(Method.DELETE)

        val prefersHeaders = mutableListOf("return=${returning.identifier}")
        if (count != null) {
            prefersHeaders.add("count=${count}")
        }
        setHeader("Prefer", prefersHeaders.joinToString(","))

        return PostgrestFilterBuilder(this)
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