package io.supabase.postgrest

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.supabase.postgrest.builder.Count
import org.junit.ClassRule
import org.junit.jupiter.api.*
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager

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
            Thread.sleep(5000)
        }

        @Suppress("unused")
        @AfterAll
        @JvmStatic
        fun afterAll() {
            dockerEnv.stop()
        }

    }

    private val postgrestClient = PostgrestDefaultClient(URI("http://localhost:3111"))
    private val tableUsers = "users"

    @BeforeEach
    fun setupDatabase() {
        dbConnection().use { connection ->
            executeSql(connection, loadResourceContent("/00-schema.sql"))
            executeSql(connection, loadResourceContent("/01-dummy-data.sql"))
        }
    }

    private fun loadResourceContent(path: String): String {
        return PostgresClientIntegrationTest::class.java.getResource(path).readText()
    }

    @Test
    fun `should fetch single`() {
        val userFromPostgrest = postgrestClient.from<User>(tableUsers)
                .select()
                .eq(User::username, "supabot")
                .single()
                .executeAndGetSingle<User>()

        assertThat(userFromPostgrest).isEqualTo(
                User(
                        username = "supabot",
                        status = "ONLINE",
                        age_range = "[1,2)",
                        catchphrase = "'cat' 'fat'",
                        data = null
                )
        )
    }

    @Test
    fun `should fetch multiple`() {
        val usersFromPostgrest = postgrestClient.from<User>(tableUsers)
                .select()
                .executeAndGetList<User>()

        assertThat(usersFromPostgrest).hasSize(4)
    }

    @Nested
    inner class InsertUpdateDelete {

        @Test
        fun `basic insert`() {
            val message = mapOf("message" to "foo", "username" to "supabot", "channel_id" to 1)
            postgrestClient.from<Any>("messages")
                    .insert(message)
                    .execute()

            val dataFromPostgres = postgrestClient.from<Any>("messages")
                    .select()
                    .eq("message", "foo")
                    .limit(1)
                    .single()
                    .executeAndGetSingle<Map<String, Any>>()

            assertk.assertAll {
                message.forEach { (key, value) -> assertThat(dataFromPostgres[key]).isEqualTo(value) }
            }
        }

        @Test
        fun `upsert`() {
            val updatedMessage = mapOf("id" to 3, "message" to "foo", "username" to "supabot", "channel_id" to 1)
            postgrestClient.from<Any>("messages")
                    .insert(
                            value = updatedMessage,
                            upsert = true
                    )
                    .execute()

            val dataFromPostgres = postgrestClient.from<Any>("messages")
                    .select()
                    .eq("id", 3)
                    .limit(1)
                    .single()
                    .executeAndGetSingle<Map<String, Any>>()

            assertk.assertAll {
                updatedMessage.forEach { (key, value) -> assertThat(dataFromPostgres[key]).isEqualTo(value) }
            }
        }

        @Test
        fun `bulk insert`() {
            postgrestClient.from<Any>("messages")
                    .insert(
                            values = listOf(
                                    mapOf("message" to "foo", "username" to "supabot", "channel_id" to 1),
                                    mapOf("message" to "foo", "username" to "supabot", "channel_id" to 1),
                            )
                    )
                    .execute()

            val response = postgrestClient.from<Any>("messages")
                    .select(count = Count.EXACT)
                    .execute()

            assertThat(response.count).isEqualTo(4)
        }

        @Test
        fun `basic update`() {
            val updateValues = mapOf("data" to mapOf("foo" to 1))

            postgrestClient.from<Any>("messages")
                    .update(updateValues)
                    .eq("message", "Perfection is attained.")
                    .execute()

            val updatedEntry = postgrestClient.from<Any>("messages")
                    .select()
                    .eq("message", "Perfection is attained.")
                    .limit(1)
                    .single()
                    .executeAndGetSingle<Map<String, Any>>()

            assertThat(updatedEntry["data"]).isEqualTo(updateValues["data"])
        }

        @Test
        fun `basic delete`() {
            postgrestClient.from<Any>("messages")
                    .delete()
                    .eq("message", "Perfection is attained.")
                    .execute()

            val response = postgrestClient.from<Any>("messages")
                    .select(count = Count.EXACT)
                    .execute()

            assertThat(response.count).isEqualTo(1)
        }
    }

    @Test
    fun `select with head`() {
        // TODO
    }

    @Test
    fun `stored procedure`() {
        // TODO
    }

    private fun testUser(): User {
        return User(
                username = "kevcodez",
                data = emptyMap(),
                age_range = null,
                status = "ONLINE",
                catchphrase = null
        )
    }

    private fun executeSql(connection: Connection, sql: String): Boolean {
        return connection.createStatement().use { statement ->
            return@use statement.execute(sql)
        }
    }

    private fun dbConnection(): Connection {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5999/app_db?loginTimeout=10&socketTimeout=10&ssl=false", "app_user", "password")
    }
}

data class User(
        val username: String,
        val data: Map<String, Any>?,
        val age_range: String?,

        val status: String,
        val catchphrase: String?

)
