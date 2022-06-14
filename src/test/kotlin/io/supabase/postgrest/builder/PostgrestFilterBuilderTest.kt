package io.supabase.postgrest.builder

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.reflect.KProperty1

internal class PostgrestFilterBuilderTest {

    private val postgrestBuilderMock = PostgrestBuilder<Any>(URI(""), mockk(), mockk(), emptyMap(), null)

    private var filterBuilder: PostgrestFilterBuilder<Any>? = null

    @BeforeEach
    fun beforeEach() {
        filterBuilder = PostgrestFilterBuilder(postgrestBuilderMock)
    }

    @Test
    fun not() {
        filterBuilder!!.not("columnName", FilterOperator.CD, "val")
        assertSearchParam("columnName", "not.cd.val")
    }

    @Test
    fun or() {
        filterBuilder!!.or("fff")
        assertSearchParam("or", "(fff)")
    }

    @Test
    fun eq() {
        filterBuilder!!.eq("columnName", "val")
        assertSearchParam("columnName", "eq.val")
    }

    @Test
    fun neq() {
        filterBuilder!!.neq("columnName", "val")
        assertSearchParam("columnName", "neq.val")
    }

    @Test
    fun gt() {
        filterBuilder!!.gt("columnName", "val")
        assertSearchParam("columnName", "gt.val")
    }

    @Test
    fun gte() {
        filterBuilder!!.gte("columnName", "val")
        assertSearchParam("columnName", "gte.val")
    }

    @Test
    fun lt() {
        filterBuilder!!.lt("columnName", "val")
        assertSearchParam("columnName", "lt.val")
    }

    @Test
    fun lte() {
        filterBuilder!!.lte("columnName", "val")
        assertSearchParam("columnName", "lte.val")
    }

    @Test
    fun like(){
        filterBuilder!!.like("columnName", "val")
        assertSearchParam("columnName", "like.val")
    }

    @Test
    fun ilike (){
        filterBuilder!!.ilike("columnName", "val")
        assertSearchParam("columnName", "ilike.val")
    }

    @Test
    fun `is`() {
        filterBuilder!!.`is`("columnName", true)
        assertSearchParam("columnName", "is.true")
    }

    @Test
    fun `in`() {
        filterBuilder!!.`in`("columnName", listOf("val1", "val2"))
        assertSearchParam("columnName", """in.("val1","val2")""")
    }

    @Test
    fun rangeLt() {
        filterBuilder!!.rangeLt("columnName", "val")
        assertSearchParam("columnName", "sl.val")
    }

    @Test
    fun rangeGt() {
        filterBuilder!!.rangeGt("columnName", "val")
        assertSearchParam("columnName", "sr.val")
    }

    @Test
    fun rangeGte(){
        filterBuilder!!.rangeGte("columnName", "val")
        assertSearchParam("columnName", "nxl.val")
    }

    @Test
    fun rangeLte(){
        filterBuilder!!.rangeLte("columnName", "val")
        assertSearchParam("columnName", "nxr.val")
    }

    @Test
    fun adjacent() {
        filterBuilder!!.adjacent("columnName", "val")
        assertSearchParam("columnName", "adj.val")
    }

    @Test
    fun `text search without config`() {
        filterBuilder!!.textSearch("columnName", "val" ,TextSearchType.PHRASETO)
        assertSearchParam("columnName", "phraseto.val")
    }

    @Test
    fun `text search with config`() {
        filterBuilder!!.textSearch("columnName", "val" ,TextSearchType.PHRASETO, "config")
        assertSearchParam("columnName", "phraseto(config).val")
    }

    @Test
    fun `text search phrase to`() {
        filterBuilder!!.textSearch("columnName", "val" ,TextSearchType.PHRASETO)
        assertSearchParam("columnName", "phraseto.val")
    }

    @Test
    fun `text search plain to`() {
        filterBuilder!!.textSearch("columnName", "val" ,TextSearchType.PLAINTO)
        assertSearchParam("columnName", "plainto.val")
    }

    @Test
    fun `text search tsvector`() {
        filterBuilder!!.textSearch("columnName", "val" ,TextSearchType.TSVECTOR)
        assertSearchParam("columnName", "tsvector.val")
    }

    @Test
    fun `text search websearch`() {
        filterBuilder!!.textSearch("columnName", "val" ,TextSearchType.WEBSEARCH)
        assertSearchParam("columnName", "websearch.val")
    }

    @Test
    fun filter() {
        filterBuilder!!.filter("columnName", FilterOperator.ADJ, "val")
        assertSearchParam("columnName", "adj.val")
    }

    @Test
    fun match() {
        filterBuilder!!.match(mapOf("foo" to "bar", "col" to 1))
        assertSearchParam("foo", "eq.bar")
        assertSearchParam("col", "eq.1")
    }

    private fun assertSearchParam(name: String, value: String) {
        val searchParams = filterBuilder!!.getSearchParams()
        assertThat(searchParams[name]).isEqualTo(value)
    }
}