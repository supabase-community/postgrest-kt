package io.supabase.postgrest.builder


import io.ktor.http.*
import io.supabase.postgrest.builder.PostgrestQueryBuilder.Companion.postrestQuery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.test.assertEquals

internal class PostgrestQueryBuilderTest {

    private var queryBuilder: PostgrestQueryBuilder<Any>? = null
    private var testSchema = ""
    private var testHeaders = mutableMapOf<String, String>()

    @BeforeEach
    fun beforeEach() {
        testHeaders = mutableMapOf()
        testSchema = ""
        queryBuilder = postrestQuery {
            url = Url(URI("123"))
            schema = testSchema
            headers = testHeaders
        }

//        queryBuilder = PostgrestQueryBuilder(URI("123"), mockk(), mockk(), emptyMap(), null)
    }

    @Nested
    inner class Select {

        @Test
        fun `basic select`() {
            queryBuilder!!.select(columns = "*")
            assertMethod(HttpMethod.Get)
            assertSearchParam("select", "*")
        }

        @Test
        fun `select with head`() {
            queryBuilder!!.select(head = true)
            assertMethod(HttpMethod.Head)
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
            assertMethod(HttpMethod.Post)
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
            assertMethod(HttpMethod.Patch)
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
            assertMethod(HttpMethod.Delete)
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
            assertMethod(HttpMethod.Post)
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
        assertEquals(queryBuilder!!.headers[name]?.first(), value)
    }

    private fun assertMethod(method: HttpMethod) {
        assertEquals(queryBuilder!!.method, method)
    }

    private fun assertBody(body: Any?) {
        assertEquals(queryBuilder!!.body, body)
    }

    private fun assertSearchParam(name: String, value: String) {
        val searchParams = queryBuilder!!.searchParams
        assertEquals(searchParams[name], value)
    }

}