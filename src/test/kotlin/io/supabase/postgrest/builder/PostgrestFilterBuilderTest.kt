package io.supabase.postgrest.builder

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PostgrestFilterBuilderTest {

    private val postgrestBuilderMock = mockk<PostgrestBuilder<Any>>()
    private var filterBuilder: PostgrestFilterBuilder<Any>? = null

    @BeforeEach
    fun beforeEach() {
        filterBuilder = PostgrestFilterBuilder(postgrestBuilderMock)
    }

    @Test
    fun `not`() {
        filterBuilder!!.not("columnName", FilterOperator.CD, "val")
        assertSearchParam("columnName", "not.cd.val")
    }

    @Test
    fun `or`() {
        filterBuilder!!.or("fff")
        assertSearchParam("or", "(fff)")
    }

    @Test
    fun `eq`() {
        filterBuilder!!.eq("columnName", "val")
        assertSearchParam("columnName", "eq.val")
    }

    @Test
    fun `neq`() {
        filterBuilder!!.neq("columnName", "val")
        assertSearchParam("columnName", "neq.val")
    }

    @Test
    fun `gt`() {
        filterBuilder!!.gt("columnName", "val")
        assertSearchParam("columnName", "gt.val")
    }

    @Test
    fun `gte`() {
        filterBuilder!!.gte("columnName", "val")
        assertSearchParam("columnName", "gte.val")
    }

    private fun assertSearchParam(name: String, value: String) {
        val searchParams = filterBuilder!!.getSearchParams()
        assertThat(searchParams[name]).isEqualTo(value)
    }
}