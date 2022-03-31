import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import io.supabase.postgrest.builder.FilterOperator
import io.supabase.postgrest.builder.PostgrestBuilder
import io.supabase.postgrest.builder.PostgrestFilterBuilder
import io.supabase.postgrest.builder.TextSearchType
import kotlin.test.Test
import kotlin.test.assertEquals

class PostgrestFilterBuilderTest {

    @property:Mock
    private val sstt = mock(classOf<String>())

    val postgrestBuilderMock: PostgrestBuilder<Any> = mock(classOf())
    private var filterBuilder: PostgrestFilterBuilder<Any>? = null

//    @BeforeTest
//    fun beforeEach() {
//        sstt
//
//        filterBuilder = PostgrestFilterBuilder(postgrestBuilderMock)
//    }

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
    fun like() {
        filterBuilder!!.like("columnName", "val")
        assertSearchParam("columnName", "like.val")
    }

    @Test
    fun ilike() {
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
    fun rangeGte() {
        filterBuilder!!.rangeGte("columnName", "val")
        assertSearchParam("columnName", "nxl.val")
    }

    @Test
    fun rangeLte() {
        filterBuilder!!.rangeLte("columnName", "val")
        assertSearchParam("columnName", "nxr.val")
    }

    @Test
    fun adjacent() {
        filterBuilder!!.adjacent("columnName", "val")
        assertSearchParam("columnName", "adj.val")
    }

    @Test
    fun text_search_without_config() {
        filterBuilder!!.textSearch("columnName", "val", TextSearchType.PHRASETO)
        assertSearchParam("columnName", "phraseto.val")
    }

    @Test
    fun text_search_with_config() {
        filterBuilder!!.textSearch("columnName", "val", TextSearchType.PHRASETO, "config")
        assertSearchParam("columnName", "phraseto(config).val")
    }

    @Test
    fun text_search_phrase_to() {
        filterBuilder!!.textSearch("columnName", "val", TextSearchType.PHRASETO)
        assertSearchParam("columnName", "phraseto.val")
    }

    @Test
    fun text_search_plain_to() {
        filterBuilder!!.textSearch("columnName", "val", TextSearchType.PLAINTO)
        assertSearchParam("columnName", "plainto.val")
    }

    @Test
    fun text_search_tsvector() {
        filterBuilder!!.textSearch("columnName", "val", TextSearchType.TSVECTOR)
        assertSearchParam("columnName", "tsvector.val")
    }

    @Test
    fun text_search_websearch() {
        filterBuilder!!.textSearch("columnName", "val", TextSearchType.WEBSEARCH)
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
        val searchParams = filterBuilder!!.searchParams
        assertEquals(searchParams[name], value)
    }
}