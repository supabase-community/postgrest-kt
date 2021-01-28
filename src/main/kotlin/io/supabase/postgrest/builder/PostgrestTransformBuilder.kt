package io.supabase.postgrest.builder

import kotlin.reflect.KProperty

open class PostgrestTransformBuilder<T : Any>(builder: PostgrestBuilder<T>) : PostgrestBuilder<T>(builder) {

    fun select(columns: String = "*"): PostgrestTransformBuilder<T> {
        val cleanedColumns = cleanColumns(columns)
        setSearchParam("select", cleanedColumns)

        return this
    }

    fun order(column: KProperty<T>, ascending: Boolean = true, nullsFirst: Boolean = false, foreignTable: String? = null): PostgrestTransformBuilder<T> {
        val key = if (foreignTable == null) "order" else """"$foreignTable".order"""
        setSearchParam(
                key,
                "${column}.${if (ascending) "asc" else "desc"}.${if (nullsFirst) "nullsfirst" else "nullslast"}"
        )

        return this
    }

    fun limit(count: Long, foreignTable: String? = null): PostgrestTransformBuilder<T> {
        val key = if (foreignTable == null) "limit" else """"$foreignTable".limit"""
        setSearchParam(key, count.toString())

        return this
    }

    fun range(from: Long, to: Long, foreignTable: String? = null): PostgrestTransformBuilder<T> {
        val keyOffset = if (foreignTable == null) "offset" else """"$foreignTable".offset"""
        val keyLimit = if (foreignTable == null) "limit" else """"$foreignTable".limit"""

        setSearchParam(keyOffset, from.toString())
        // Range is inclusive, so add 1
        setSearchParam(keyLimit, (to - from + 1).toString())

        return this
    }

    fun single(): PostgrestTransformBuilder<T> {
        setHeader(org.apache.hc.core5.http.HttpHeaders.ACCEPT, "application/vnd.pgrst.object+json")

        return this
    }

}