package io.supabase.postgrest.builder

import kotlin.reflect.KProperty1

class PostgrestFilterBuilder<T : Any>(builder: PostgrestBuilder<T>) : PostgrestTransformBuilder<T>(builder) {

    /**
     * Finds all rows which doesn't satisfy the filter.
     *
     * @param column  The column to filter on.
     * @param operator  The operator to filter with.
     * @param value  The value to filter with.
     */
    fun not(column: String, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "not.${operator.identifier}.${value}")
        return this
    }

    fun not(column: KProperty1<T, Any>, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        return not(column.name, operator, value)
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

    fun eq(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return eq(column.name, value)
    }

    /**
     * Finds all rows whose value on the stated "column" exactly matches the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun eq(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "eq.${value}")
        return this
    }


    fun neq(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "neq.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" doesn't match the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun neq(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return neq(column.name, value)
    }

    fun gt(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "gt.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is greater than the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun gt(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return gt(column.name, value)
    }

    fun gte(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "gte.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is greater than or
     * equal to the specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun gte(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return gte(column.name, value)
    }

    fun lt(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "lt.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is less than the
     * specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun lt(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return lt(column.name, value)
    }

    fun lte(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "lte.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is less than or equal
     * to the specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun lte(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return lte(column.name, value)
    }

    fun like(column: String, pattern: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "like.${pattern}")
        return this
    }

    /**
     * Finds all rows whose value in the stated "column" matches the supplied
     * "pattern" (case sensitive).
     *
     * @param column  The column to filter on.
     * @param pattern  The pattern to filter with.
     */
    fun like(column: KProperty1<T, Any>, pattern: String): PostgrestFilterBuilder<T> {
        return like(column.name, pattern)
    }

    fun ilike(column: String, pattern: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "ilike.${pattern}")
        return this
    }

    /**
     * Finds all rows whose value in the stated "column" matches the supplied
     * "pattern" (case insensitive).
     *
     * @param column  The column to filter on.
     * @param pattern  The pattern to filter with.
     */
    fun ilike(column: KProperty1<T, Any>, pattern: String): PostgrestFilterBuilder<T> {
        return ilike(column.name, pattern)
    }

    fun `is`(column: String, value: Boolean?): PostgrestFilterBuilder<T> {
        setSearchParam(column, "is.${value}")
        return this
    }

    /**
     * A check for exact equality (null, true, false), finds all rows whose
     * value on the stated "column" exactly match the specified "value".
     *
     * @param column  The column to filter on.
     * @param value  The value to filter with.
     */
    fun `is`(column: KProperty1<T, Any>, value: Boolean?): PostgrestFilterBuilder<T> {
        return `is`(column.name, value)
    }

    fun `in`(column: String, values: List<Any>): PostgrestFilterBuilder<T> {
        setSearchParam(column, "in.(${cleanFilterArray(values)})")
        return this
    }

    /**
     * Finds all rows whose value on the stated "column" is found on the
     * specified "values".
     *
     * @param column  The column to filter on.
     * @param values  The values to filter with.
     */
    fun `in`(column: KProperty1<T, Any>, values: List<Any>): PostgrestFilterBuilder<T> {
        return `in`(column.name, values)
    }

    private fun cleanFilterArray(values: List<Any>): String {
        return values.joinToString(",") { s -> """"$s"""" }
    }

    fun rangeLt(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "sl.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" is strictly to the
     * left of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun rangeLt(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeLt(column.name, range)
    }

    fun rangeGt(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "sr.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" is strictly to
     * the right of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun rangeGt(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeGt(column.name, range)
    }

    fun rangeGte(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "nxl.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" does not extend
     * to the left of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun rangeGte(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeGte(column.name, range)
    }

    fun rangeLte(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "nxr.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" does not extend
     * to the right of the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun rangeLte(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeLte(column.name, range)
    }

    fun adjacent(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "adj.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated "column" is adjacent to
     * the specified "range".
     *
     * @param column  The column to filter on.
     * @param range  The range to filter with.
     */
    fun adjacent(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return adjacent(column.name, range)
    }

    fun textSearch(column: String, query: String, textSearchType: TextSearchType, config: String? = null): PostgrestFilterBuilder<T> {
        val configPart = if (config === null) "" else "(${config})"
        setSearchParam(column, "${textSearchType.identifier}${configPart}.${query}")
        return this
    }

    fun textSearch(column: KProperty1<T, Any>, query: String, textSearchType: TextSearchType, config: String? = null): PostgrestFilterBuilder<T> {
        return textSearch(column.name, query, textSearchType, config)
    }

    fun filter(column: String, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "${operator.identifier}.${value}")
        return this
    }

    /**
     * Finds all rows whose "column" satisfies the filter.
     *
     * @param column  The column to filter on.
     * @param operator  The operator to filter with.
     * @param value  The value to filter with.
     */
    fun filter(column: KProperty1<T, Any>, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        return filter(column.name, operator, value)
    }

    fun match(query: Map<String, Any>): PostgrestFilterBuilder<T> {
        query.entries.forEach { (name, value) -> setSearchParam(name, "eq.$value") }
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

enum class TextSearchType(val identifier: String) {
    TSVECTOR("tsvector"),
    PLAINTO("plainto"),
    PHRASETO("phraseto"),
    WEBSEARCH("websearch")
}