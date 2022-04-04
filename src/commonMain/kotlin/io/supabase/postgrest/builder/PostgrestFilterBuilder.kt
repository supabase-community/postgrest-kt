package io.supabase.postgrest.builder

import io.ktor.http.*
import io.supabase.postgrest.http.PostgrestHttpClient
import kotlin.reflect.KProperty1

open class PostgrestFilterBuilder<T : Any> : PostgrestTransformBuilder<T> {

    constructor(url: Url, schema: String? = null, headers: Headers, httpClient: PostgrestHttpClient)
            : super(url, schema, headers, httpClient)

    internal constructor(builder: PostgrestBuilder<T>) : super(builder)

    /**
     * Finds all rows which doesn't satisfy the filter.
     *
     * @param[column] The column to filter on.
     * @param[operator] The operator to filter with.
     * @param[value] The value to filter with.
     */
    fun not(column: String, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "not.${operator.identifier}.${value}")
        return this
    }

    /**
     * Finds all rows which doesn't satisfy the filter.
     *
     * @param[column] The column to filter on.
     * @param[operator] The operator to filter with.
     * @param[value] The value to filter with.
     */
    fun not(column: KProperty1<T, Any>, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        return not(column.columnName, operator, value)
    }

    /**
     * Finds all rows satisfying at least one of the [filters].
     *
     * @param[filters] The filters to use, separated by commas.
     */
    fun or(filters: String): PostgrestFilterBuilder<T> {
        setSearchParam("or", "(${filters})")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] exactly matches the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun eq(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return eq(column.columnName, value)
    }

    /**
     * Finds all rows whose value on the stated [column] exactly matches the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun eq(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "eq.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] doesn't match the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun neq(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "neq.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] doesn't match the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun neq(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return neq(column.columnName, value)
    }

    /**
     * Finds all rows whose value on the stated [column] is greater than the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun gt(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "gt.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] is greater than the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun gt(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return gt(column.columnName, value)
    }

    /**
     * Finds all rows whose value on the stated [column] is greater than or
     * equal to the specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun gte(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "gte.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] is greater than or
     * equal to the specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun gte(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return gte(column.columnName, value)
    }

    /**
     * Finds all rows whose value on the stated [column] is less than the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun lt(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "lt.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] is less than the
     * specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun lt(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return lt(column.columnName, value)
    }

    /**
     * Finds all rows whose value on the stated [column] is less than or equal
     * to the specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun lte(column: String, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "lte.${value}")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] is less than or equal
     * to the specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun lte(column: KProperty1<T, Any>, value: Any): PostgrestFilterBuilder<T> {
        return lte(column.columnName, value)
    }

    /**
     * Finds all rows whose value in the stated [column] matches the supplied
     * [pattern] (case sensitive).
     *
     * @param[column] The column to filter on.
     * @param[pattern] The pattern to filter with.
     */
    fun like(column: String, pattern: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "like.${pattern}")
        return this
    }

    /**
     * Finds all rows whose value in the stated [column] matches the supplied
     * [pattern] (case sensitive).
     *
     * @param[column] The column to filter on.
     * @param[pattern] The pattern to filter with.
     */
    fun like(column: KProperty1<T, Any>, pattern: String): PostgrestFilterBuilder<T> {
        return like(column.columnName, pattern)
    }

    /**
     * Finds all rows whose value in the stated [column] matches the supplied
     * [pattern] (case insensitive).
     *
     * @param[column] The column to filter on.
     * @param[pattern] The pattern to filter with.
     */
    fun ilike(column: String, pattern: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "ilike.${pattern}")
        return this
    }

    /**
     * Finds all rows whose value in the stated [column] matches the supplied
     * [pattern] (case insensitive).
     *
     * @param[column] The column to filter on.
     * @param[pattern] The pattern to filter with.
     */
    fun ilike(column: KProperty1<T, Any>, pattern: String): PostgrestFilterBuilder<T> {
        return ilike(column.columnName, pattern)
    }

    /**
     * A check for exact equality (null, true, false), finds all rows whose
     * value on the stated [column] exactly match the specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun `is`(column: String, value: Boolean?): PostgrestFilterBuilder<T> {
        setSearchParam(column, "is.${value}")
        return this
    }

    /**
     * A check for exact equality (null, true, false), finds all rows whose
     * value on the stated [column] exactly match the specified [value].
     *
     * @param[column] The column to filter on.
     * @param[value] The value to filter with.
     */
    fun `is`(column: KProperty1<T, Any>, value: Boolean?): PostgrestFilterBuilder<T> {
        return `is`(column.columnName, value)
    }

    /**
     * Finds all rows whose value on the stated [column] is found on the
     * specified [values].
     *
     * @param[column] The column to filter on.
     * @param[values] The values to filter with.
     */
    fun `in`(column: String, values: List<Any>): PostgrestFilterBuilder<T> {
        setSearchParam(column, "in.(${cleanFilterArray(values)})")
        return this
    }

    /**
     * Finds all rows whose value on the stated [column] is found on the
     * specified [values].
     *
     * @param[column] The column to filter on.
     * @param[values] The values to filter with.
     */
    fun `in`(column: KProperty1<T, Any>, values: List<Any>): PostgrestFilterBuilder<T> {
        return `in`(column.columnName, values)
    }

    private fun cleanFilterArray(values: List<Any>): String {
        return values.joinToString(",") { s -> """"$s"""" }
    }

    /**
     * Finds all rows whose range value on the stated [column] is strictly to the
     * left of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeLt(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "sl.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated [column] is strictly to the
     * left of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeLt(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeLt(column.columnName, range)
    }

    /**
     * Finds all rows whose range value on the stated [column] is strictly to
     * the right of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeGt(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "sr.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated [column] is strictly to
     * the right of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeGt(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeGt(column.columnName, range)
    }

    /**
     * Finds all rows whose range value on the stated [column] does not extend
     * to the left of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeGte(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "nxl.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated [column] does not extend
     * to the left of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeGte(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeGte(column.columnName, range)
    }

    /**
     * Finds all rows whose range value on the stated [column] does not extend
     * to the right of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeLte(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "nxr.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated [column] does not extend
     * to the right of the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun rangeLte(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return rangeLte(column.columnName, range)
    }

    /**
     * Finds all rows whose range value on the stated [column] is adjacent to
     * the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun adjacent(column: String, range: String): PostgrestFilterBuilder<T> {
        setSearchParam(column, "adj.${range}")
        return this
    }

    /**
     * Finds all rows whose range value on the stated [column] is adjacent to
     * the specified [range].
     *
     * @param[column] The column to filter on.
     * @param[range] The range to filter with.
     */
    fun adjacent(column: KProperty1<T, Any>, range: String): PostgrestFilterBuilder<T> {
        return adjacent(column.columnName, range)
    }

    /**
     * Finds all rows whose tsvector value on the stated [column] matches
     * [query] using the given [textSearchType].
     *
     * @param [column] The column to filter on.
     * @param [query] The Postgres tsquery string to filter with.
     * @param [textSearchType] The Postgres text search type.
     * @param [config] The text search configuration to use.
     */
    fun textSearch(
        column: String,
        query: String,
        textSearchType: TextSearchType,
        config: String? = null
    ): PostgrestFilterBuilder<T> {
        val configPart = if (config === null) "" else "(${config})"
        setSearchParam(column, "${textSearchType.identifier}${configPart}.${query}")
        return this
    }

    fun textSearch(
        column: KProperty1<T, Any>,
        query: String,
        textSearchType: TextSearchType,
        config: String? = null
    ): PostgrestFilterBuilder<T> {
        return textSearch(column.columnName, query, textSearchType, config)
    }

    /**
     * Finds all rows whose [column] satisfies the filter.
     *
     * @param[column] The column to filter on.
     * @param[operator] The operator to filter with.
     * @param[value] The value to filter with.
     */
    fun filter(column: String, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        setSearchParam(column, "${operator.identifier}.${value}")
        return this
    }

    /**
     * Finds all rows whose [column] satisfies the filter.
     *
     * @param[column] The column to filter on.
     * @param[operator] The operator to filter with.
     * @param[value] The value to filter with.
     */
    fun filter(column: KProperty1<T, Any>, operator: FilterOperator, value: Any): PostgrestFilterBuilder<T> {
        return filter(column.columnName, operator, value)
    }

    /**
     * Finds all rows whose columns match the specified [query] object.
     *
     * @param[query] The object to filter with, with column names as keys mapped to their filter values.
     */
    fun match(query: Map<String, Any>): PostgrestFilterBuilder<T> {
        query.entries.forEach { (name, value) -> setSearchParam(name, "eq.$value") }
        return this
    }

    private val KProperty1<T, Any>.columnName: String
        get() {
            return this.name.lowercase()
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