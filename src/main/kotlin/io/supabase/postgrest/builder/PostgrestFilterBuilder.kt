package io.supabase.postgrest.builder

import kotlin.reflect.KProperty

class PostgrestFilterBuilder<T : Any>(builder: PostgrestBuilder<T>) : PostgrestTransformBuilder<T>(builder) {

    /**
     * Finds all rows which doesn't satisfy the filter.
     *
     * @param column  The column to filter on.
     * @param operator  The operator to filter with.
     * @param value  The value to filter with.
     */
    fun not(column: KProperty<T>, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "not.${operator.identifier}.${value}")
        return this
    }

    /**
     * Finds all rows satisfying at least one of the filters.
     *
     * @param filters  The filters to use, separated by commas.
     */
    fun or(filters: String): PostgrestFilterBuilder<T> {
        setSearchParam("or", "(${filters})")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" exactly matches the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun eq(column: KProperty<T>, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "eq.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" doesn't match the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun neq(column: KProperty<T>, value: T): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "neq.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is greater than the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun gt(column: KProperty<T>, value: T): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "gt.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is greater than or
     * equal to the specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun gte(column: KProperty<T>, value: T): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "gte.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is less than the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun lt(column: KProperty<T>, value: T): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "lt.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is less than or equal
     * to the specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun lte(column: KProperty<T>, value: T): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "lte.${value}")
        return this
    }

    /**
     * Finds all rows whose value in the stated "column" matches the supplied
     * "pattern" (case sensitive).
     *
     * @param column  The column to filter on.
     * @param pattern  The pattern to filter with.
     */
    fun like(column: KProperty<T>, pattern: String): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "like.${pattern}")
        return this
    }

    /**
     * Finds all rows whose value in the stated "column" matches the supplied
     * "pattern" (case insensitive).
     *
     * @param column  The column to filter on.
     * @param pattern  The pattern to filter with.
     */
    fun ilike(column: KProperty<T>, pattern: String): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "ilike.${pattern}")
        return this
    }

    /**
     * A check for exact equality (null, true, false), finds all rows whose
     * value on the stated "column" exactly match the specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun `is`(column: KProperty<T>, value: Boolean?): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "is.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is found on the
     * specified "values".
     *
     * @param column  The column to filter on.
     * @param values  The values to filter with.
     */
    fun `in`(column: KProperty<T>, values: List<Any>): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "in.(${cleanFilterArray(values)})")
        return this
    }

    private fun cleanFilterArray(values: List<Any>): String {
        return values.joinToString(",") { s -> """"$s"""" }
    }

    /**
     * Finds all rows whose range value on the stated "column" is strictly to the
     * left of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun sl(column: KProperty<T>, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "sl.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" is strictly to
     * the right of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun sr(column: KProperty<T>, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "sr.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" does not extend
     * to the left of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun nxl(column: KProperty<T>, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "nxl.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" does not extend
     * to the right of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun nxr(column: KProperty<T>, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "nxr.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" is adjacent to
     * the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun adj(column: KProperty<T>, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "adj.${range}")
        return this
    }

    /**
     * Finds all rows whose tsvector value on the stated "column" matches
     * to_tsquery("query").
     *
     * @param column  The column to filter on.
     * @param query  The Postgres tsquery String to filter with.
     * @param config  The text search configuration to use.
     */
    fun fts(column: KProperty<T>, query: String, config: String? = null): PostgrestFilterBuilder<T> {
        val configPart = if (config === null) "" else "(${config})"
        setSearchParam(column.name, "fts${configPart}.${query}")
        return this
    }

    /**
     * Finds all rows whose tsvector value on the stated "column" matches
     * plainto_tsquery("query").
     *
     * @param column  The column to filter on.
     * @param query  The Postgres tsquery String to filter with.
     * @param config  The text search configuration to use.
     */
    fun plfts(column: KProperty<T>, query: String, config: String? = null): PostgrestFilterBuilder<T> {
        val configPart = if (config === null) "" else "(${config})"
        setSearchParam(column.name, "plfts${configPart}.${query}")
        return this
    }

    /**
     * Finds all rows whose tsvector value on the stated "column" matches
     * phraseto_tsquery("query").
     *
     * @param column  The column to filter on.
     * @param query  The Postgres tsquery String to filter with.
     * @param config  The text search configuration to use.
     */
    fun phfts(column: KProperty<T>, query: String, config: String? = null): PostgrestFilterBuilder<T> {
        val configPart = if (config === null) "" else "(${config})"
        setSearchParam(column.name, "phfts${configPart}.${query}")
        return this
    }

    /**
     * Finds all rows whose tsvector value on the stated "column" matches
     * websearch_to_tsquery("query").
     *
     * @param column  The column to filter on.
     * @param query  The Postgres tsquery String to filter with.
     * @param config  The text search configuration to use.
     */
    fun wfts(column: KProperty<T>, query: String, config: String? = null): PostgrestFilterBuilder<T> {
        val configPart = if (config === null) "" else "(${config})"
        setSearchParam(column.name, "wfts${configPart}.${query}")
        return this
    }

    /**
     * Finds all rows whose "column" satisfies the filter.
     *
     * @param column  The column to filter on.
     * @param operator  The operator to filter with.
     * @param value  The value to filter with.
     */
    fun filter(column: KProperty<T>, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column.name, "${operator.identifier}.${value}")
        return this
    }
}

enum class FilterOperator(val identifier: String) {
    EQ("eq"),
    NEQ("neq"),
    GT("gt"),
    GTE("gte"),
    LT("lt"),
    LTE("lte"),
    LIKE("like"),
    ILIKE("ilike"),
    IS("is"),
    IN("in"),
    CS("cs"),
    CD("cd"),
    SL("sl"),
    SR("sr"),
    NXL("nxl"),
    NXR("nxr"),
    ADJ("adj"),
    OV("ov"),
    FTS("fts"),
    PLFTS("plfts"),
    PHFTS("phfts"),
    WFTS("wfts"),
}
