package io.supabase.postgrest

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.ClassRule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate

@Testcontainers
class PostgresClientTest {

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
            executeSql(connection, "DROP SCHEMA public CASCADE;")
            executeSql(connection, "CREATE SCHEMA public;")

            executeSql(connection, """
            CREATE TABLE $tableUsers(
                id BIGINT PRIMARY KEY,
                name VARCHAR NOT NULL,
                birthday DATE NOT NULL,
                twitter_followers BIGINT NOT NULL
            );
        """)
        }

    }

    @Test
    fun `should insert and fetch single`() {
        val user = testUser()

        postgrestClient.from<User>(tableUsers)
                .insert(user)
                .execute()

        val userFromPostgrest = postgrestClient.from<User>(tableUsers)
                .select()
                .eq(User::name, user.name)
                .single()
                .executeAndGetSingle<User>()

        assertThat(userFromPostgrest).isEqualTo(user)
    }

    @Test
    fun `should insert and fetch multiple`() {
        val user1 = testUser()
        val user2 = user1.copy(id = 2L, name = "something-else")

        postgrestClient.from<User>(tableUsers)
                .insert(listOf(user1, user2))
                .execute()

        val usersFromPostgrest = postgrestClient.from<User>(tableUsers)
                .select()
                .executeAndGetList<User>()

        assertThat(usersFromPostgrest).hasSize(2)
        assertThat(usersFromPostgrest).containsAll(user1, user2)
    }

    private fun executeSql(connection: Connection, sql: String): Boolean {
        return connection.createStatement().use { statement ->
            return@use statement.execute(sql)
        }
    }

    private fun dbConnection(): Connection {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5999/app_db?loginTimeout=10&socketTimeout=10&ssl=false", "app_user", "password")
    }

    private fun testUser(): User {
        return User(
                id = 1,
                name = "kevcodez",
                birthday = LocalDate.now(),
                twitterFollowers = 4
        )
    }
}

data class User(
        val id: Long,
        val name: String,
        val birthday: LocalDate,

        @JsonProperty("twitter_followers")
        val twitterFollowers: Int
)