package io.supabase.postgrest.builder

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.mockk
import org.apache.hc.core5.http.Method
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI

internal class PostgrestQueryBuilderTest {

    private var queryBuilder: PostgrestQueryBuilder<Any>? = null

    @BeforeEach
    fun beforeEach() {
        queryBuilder = PostgrestQueryBuilder(URI("123"), mockk(), mockk(), emptyMap(), null)
    }

    @Nested
    inner class Select {

        @Test
        fun `basic select`() {
            queryBuilder!!.select(columns = "*")
            assertMethod(Method.GET)
            assertSearchParam("select", "*")
        }

        @Test
        fun `select with head`() {
            queryBuilder!!.select(head = true)
            assertMethod(Method.HEAD)
        }

        @Test
        fun `select with count`() {
            queryBuilder!!.select(count = Count.PLANNED)
            assertHeader("Prefer", "count=planned")
        }
    }

    @Nested
    inner class Insert {

        private val value = mapOf("foo" to "bar")

        @Test
        fun `insert single`() {
            queryBuilder!!.insert(value = value)
            assertMethod(Method.POST)
            assertBody(listOf(value))
        }

        @Test
        fun `insert multiple`() {
            val values = listOf(value, value)
            queryBuilder!!.insert(values = values)
            assertBody(values)
        }

        @Test
        fun `insert with count`() {
            queryBuilder!!.insert(value = value, count = Count.ESTIMATED)
            assertHeader("Prefer", "return=representation,count=estimated")
        }

        @Test
        fun `insert with different returning`() {
            queryBuilder!!.insert(value = value, returning = Returning.MINIMAL)
            assertHeader("Prefer", "return=minimal")
        }

        @Test
        fun `upsert`() {
            queryBuilder!!.insert(value = value, upsert = true)
            assertHeader("Prefer", "return=representation,resolution=merge-duplicates")
        }

        @Test
        fun `upsert with count`() {
            queryBuilder!!.insert(value = value, upsert = true, count = Count.EXACT)
            assertHeader("Prefer", "return=representation,resolution=merge-duplicates,count=exact")
        }

        @Test
        fun `upsert with onconflict`() {
            queryBuilder!!.insert(value = value, upsert = true, onConflict = "foobar")
            assertHeader("Prefer", "return=representation,resolution=merge-duplicates")
            assertSearchParam("on_conflict", "foobar")
        }
    }

    @Nested
    inner class Update {

        private val value = mapOf("foo" to "bar")

        @Test
        fun `update`() {
            queryBuilder!!.update(value = value)
            assertMethod(Method.PATCH)
        }

        @Test
        fun `update with count`() {
            queryBuilder!!.update(value = value, count = Count.EXACT)
            assertHeader("Prefer", "return=representation,count=exact")
        }

        @Test
        fun `insert with different returning`() {
            queryBuilder!!.update(value = value, returning = Returning.MINIMAL)
            assertHeader("Prefer", "return=minimal")
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `delete`() {
            queryBuilder!!.delete()
            assertMethod(Method.DELETE)
        }

        @Test
        fun `delete with count`() {
            queryBuilder!!.delete(count = Count.PLANNED)
            assertHeader("Prefer", "return=representation,count=planned")
        }

        @Test
        fun `delete with different returning`() {
            queryBuilder!!.delete(returning = Returning.MINIMAL)
            assertHeader("Prefer", "return=minimal")
        }
    }

    @Nested
    inner class Rpc {

        @Test
        fun `rpc without body`() {
            queryBuilder!!.rpc(null)
            assertMethod(Method.POST)
            assertBody(null)
        }

        @Test
        fun `rpc with body`() {
            val params = mapOf("foo" to "bar")
            queryBuilder!!.rpc(params)
            assertBody(params)
        }
    }

    private fun assertHeader(name: String, value: String) {
        assertThat(queryBuilder!!.getHeaders()[name]).isEqualTo(value)
    }

    private fun assertMethod(method: Method) {
        assertThat(queryBuilder!!.getMethod()).isEqualTo(method)
    }

    private fun assertBody(body: Any?) {
        assertThat(queryBuilder!!.getBody()).isEqualTo(body)
    }

    private fun assertSearchParam(name: String, value: String) {
        val searchParams = queryBuilder!!.getSearchParams()
        assertThat(searchParams[name]).isEqualTo(value)
    }

}