package io.supabase.postgrest

import org.junit.jupiter.api.Test
import java.net.URI

class PostgresClientTest {

    @Test
    fun foo() {
        val client = PostgrestDefaultClient(
                uri = URI("https://eyimuvrqyphojiqwapfv.supabase.co/rest/v1"),
                defaultHeaders = mapOf("apiKey" to "xyz")
        )

        val a = client.from<Foo>("foo")
                .select()
                .execute()
    }
}

data class Foo(
        val id: Long,
        val text: String
)