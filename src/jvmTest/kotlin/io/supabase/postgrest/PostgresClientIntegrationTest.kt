package io.supabase.postgrest

import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.supabase.postgrest.builder.executeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.ClassRule
import org.junit.jupiter.api.*
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.sql.DriverManager
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@Testcontainers
open class PostgresClientIntegrationTest {
    // Docker compose setup to run Postgres + PostgREST
    companion object {
        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)

        @ClassRule
        @JvmField
        val dockerEnv = KDockerComposeContainer(File("src/test/resources/docker-compose.yml"))


        @Suppress("unused")
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            dockerEnv.start()

            // YIKES - find a better way to avoid connection issues
            Thread.sleep(30000)
        }

        @Suppress("unused")
        @AfterAll
        @JvmStatic
        fun afterAll() {
            dockerEnv.stop()
        }

    }

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private val postgrestClient =
        PostgrestDefaultClient(uri = Url("http://127.0.0.1:3111"), clientEngine = Apache.create { })
    private val tableUsers = "users"

    @BeforeTest
    fun setupDatabase() {
        dbConnection().use { connection ->
            executeSql(connection, loadResourceContent("/00-schema.sql"))
            executeSql(connection, loadResourceContent("/01-dummy-data.sql"))
        }

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun `should fetch single`() = runTest {
        val userFromPostgrest = postgrestClient.from<User>(tableUsers)
            .select()
            .eq(User::username, "supabot")
            .single()
            .executeCall<User>()

        assertTrue(userFromPostgrest.isSuccess)

        assertEquals(
            User(
                username = "supabot",
                status = "ONLINE",
                age_range = "[1,2)",
                catchphrase = "'cat' 'fat'",
                data = null
            ), userFromPostgrest.getOrThrow().body
        )
    }

    @Test
    fun `should fetch multiple`() = runTest {
        val usersFromPostgrest = postgrestClient.from<User>(tableUsers)
            .select()
            .executeCall<List<User>>()

        assertTrue(usersFromPostgrest.getOrThrow().body.size == 4)
    }


    @Nested
    inner class InsertUpdateDelete {

        @Test
        fun `basic insert`() = runTest {
            val message = mapOf("message" to "foo", "username" to "supabot", "channel_id" to 1)
            val insertResult = postgrestClient.from<Any>("messages")
                .insert(message)
                .executeCall<String>()

            assertTrue(insertResult.isSuccess)

            val dataFromPostgres = postgrestClient.from<Any>("messages")
                .select()
                .eq("message", "foo")
                .limit(1)
                .single()
                .executeCall<Map<String, Any>>()

            assertAll({
                message.forEach { (key, value) -> assertEquals(value, dataFromPostgres.getOrThrow().body[key]) }
            })
        }


        @Test
        fun `upsert`() = runTest {
            val message = Message(
                id = 3,
                message = "foo",
                channel_id = 1
            )

            val updatedMessage = mapOf("id" to 3, "message" to "foo", "username" to "supabot", "channel_id" to 1)
            val result1 = runCatching {
                postgrestClient.from<Message>("messages")
                    .insert(
                        value = message,
                        upsert = true
                    )
                    .executeCall<Any>()
            }

            assertTrue(result1.isSuccess)
            val postgrestClient2 =
                PostgrestDefaultClient(uri = Url("http://127.0.0.1:3111"), clientEngine = Apache.create { })

            val result2 = runCatching {
                postgrestClient2.from<Unit>("messages")
                    .select()
                    .eq("id", 3)
                    .limit(1)
                    .single()
                    .executeCall<Message>()
            }

            assertTrue(result2.isSuccess)

            val dataFromPostgres = result2.getOrThrow()

            assertAll({
                updatedMessage.forEach { (key, value) ->
//                    assertEquals(value.toString(), dataFromPostgres.getOrThrow().body[key])
                }

            })
        }
//
//        @Test
//        fun `bulk insert`() {
//            postgrestClient.from<Any>("messages")
//                .insert(
//                    values = listOf(
//                        mapOf("message" to "foo", "username" to "supabot", "channel_id" to 1),
//                        mapOf("message" to "foo", "username" to "supabot", "channel_id" to 1),
//                    )
//                )
//                .execute()
//
//            val response = postgrestClient.from<Any>("messages")
//                .select(count = Count.EXACT)
//                .execute()
//
//            assertThat(response.count).isEqualTo(4)
//        }
//
//        @Test
//        fun `basic update`() {
//            val updateValues = mapOf("data" to mapOf("foo" to 1))
//
//            postgrestClient.from<Any>("messages")
//                .update(updateValues)
//                .eq("message", "Perfection is attained.")
//                .execute()
//
//            val updatedEntry = postgrestClient.from<Any>("messages")
//                .select()
//                .eq("message", "Perfection is attained.")
//                .limit(1)
//                .single()
//                .executeAndGetSingle<Map<String, Any>>()
//
//            assertThat(updatedEntry["data"]).isEqualTo(updateValues["data"])
//        }
//
//        @Test
//        fun `basic delete`() {
//            postgrestClient.from<Any>("messages")
//                .delete()
//                .eq("message", "Perfection is attained.")
//                .execute()
//
//            val response = postgrestClient.from<Any>("messages")
//                .select(count = Count.EXACT)
//                .execute()
//
//            assertThat(response.count).isEqualTo(1)
//        }
    }

    @kotlinx.serialization.Serializable
    data class User(
        val username: String,
        val data: Map<String, String>?,
        val age_range: String?,

        val status: String,
        val catchphrase: String?

    )


    private fun executeSql(connection: java.sql.Connection, sql: String): Boolean {

        return connection.createStatement().use { statement ->
            return@use statement.execute(sql)
        }
    }

    private fun dbConnection(): java.sql.Connection {

        return DriverManager.getConnection(
            "jdbc:postgresql://localhost:5999/app_db?loginTimeout=100000&socketTimeout=10000&ssl=false",
            "app_user",
            "password"
        )
    }

    private fun loadResourceContent(path: String): String {
        return PostgresClientIntegrationTest::class.java.getResource(path).readText()
    }
}
