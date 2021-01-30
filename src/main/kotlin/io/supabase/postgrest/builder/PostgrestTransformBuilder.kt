package io.supabase.postgrest.builder

import kotlin.reflect.KProperty1

open class PostgrestTransformBuilder<T : Any>(builder: PostgrestBuilder<T>) : PostgrestBuilder<T>(builder) {

    /**
     * Performs vertical filtering with SELECT.
     *
     * @param[columns] The columns to retrieve, separated by commas.
     */
    fun select(columns: String = "*"): PostgrestTransformBuilder<T> {
        val cleanedColumns = cleanColumns(columns)
        setSearchParam("select", cleanedColumns)

        return this
    }

    /**
     * Orders the result with the specified [column].
     *
     * @param[column] The column to order on.
     * @param[ascending] If `true`, the result will be in ascending order.
     * @param[nullsFirst] If `true`, `null`s appear first.
     * @param[foreignTable] The foreign table to use (if `column` is a foreign column).
     */
    fun order(column: String, ascending: Boolean = true, nullsFirst: Boolean = false, foreignTable: String? = null): PostgrestTransformBuilder<T> {
        val key = if (foreignTable == null) "order" else """"$foreignTable".order"""
        setSearchParam(
                key,
                "${column}.${if (ascending) "asc" else "desc"}.${if (nullsFirst) "nullsfirst" else "nullslast"}"
        )

        return this
    }

    /**
     * Orders the result with the specified [column].
     *
     * @param[column] The column to order on.
     * @param[ascending] If `true`, the result will be in ascending order.
     * @param[nullsFirst] If `true`, `null`s appear first.
     * @param[foreignTable] The foreign table to use (if `column` is a foreign column).
     */
    fun order(column: KProperty1<T, Any>, ascending: Boolean = true, nullsFirst: Boolean = false, foreignTable: String? = null): PostgrestTransformBuilder<T> {
        return order(column.name, ascending, nullsFirst, foreignTable)
    }

    /**
     * Limits the result with the specified [count].
     *
     * @param[count] The maximum no. of rows to limit to.
     * @param[foreignTable] The foreign table to use (for foreign columns).
     */
    fun limit(count: Long, foreignTable: String? = null): PostgrestTransformBuilder<T> {
        val key = if (foreignTable == null) "limit" else """"$foreignTable".limit"""
        setSearchParam(key, count.toString())

        return this
    }

    /**
     * Limits the result to rows within the specified range, inclusive.
     *
     * @param[from] The starting index from which to limit the result, inclusive.
     * @param[to] The last index to which to limit the result, inclusive.
     * @param[foreignTable] The foreign table to use (for foreign columns).
     */
    fun range(from: Long, to: Long, foreignTable: String? = null): PostgrestTransformBuilder<T> {
        val keyOffset = if (foreignTable == null) "offset" else """"$foreignTable".offset"""
        val keyLimit = if (foreignTable == null) "limit" else """"$foreignTable".limit"""

        setSearchParam(keyOffset, from.toString())
        // Range is inclusive, so add 1
        setSearchParam(keyLimit, (to - from + 1).toString())

        return this
    }

    /**
     * Retrieves only one row from the result. Result must be one row (e.g. using `limit`),
     * otherwise this will result in an error.
     */
    fun single(): PostgrestTransformBuilder<T> {
        setHeader(org.apache.hc.core5.http.HttpHeaders.ACCEPT, "application/vnd.pgrst.object+json")

        return this
    }

}