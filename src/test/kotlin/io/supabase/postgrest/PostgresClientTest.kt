package io.supabase.postgrest

import org.junit.jupiter.api.Test
import java.net.URI

class PostgresClientTest {

    @Test
    fun `if it looks stupid but works it aint stupid`() {
        val client = PostgrestDefaultClient(
                uri = URI("https://eyimuvrqyphojiqwapfv.supabase.co/rest/v1"),
                headers = mapOf("apiKey" to "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYW5vbiIsImlhdCI6MTYxMTQ0NTk4OCwiZXhwIjoxOTI3MDIxOTg4fQ.OW1kc8pmB7Q9EO9iUSdg86cZoyx_3rLoQUJrQg35Bvs")
        )

        val dataAsList = client.from<Foo>("foo")
                .select()
                .eq(Foo::text, "asdasd")
                .executeAndGetList<Foo>()

        println(dataAsList)
    }
}

data class Foo(
        val id: Long,
        val text: String
)