package io.supabase.postgrest.http


import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostgrestHttpClientTest {

    private var mockEngineHandler: (MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData)? = null

    private val uri = Url("https://test.com")
    private val mockEngine = MockEngine { request ->
        mockEngineHandler!!(request)
    }

    private val httpClient = HttpClient(mockEngine) {
        expectSuccess = false
        install(Logging)
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private val client = PostgrestHttpClient(httpClient)


    @Test
    fun `should set http headers`() {
        val headers = mapOf("Authorization" to listOf("foobar"), "Content-Type" to listOf("application/json"))

        mockEngineHandler = {
            respond(content = "", headers = headersOf())
        }

        runBlocking {
            client.execute<Any>(
                uri = uri,
                method = HttpMethod.Get,
                headers = headers
            )

            val request = mockEngine.requestHistory.firstOrNull()
            val requestHeaders = request?.headers?.toMap()

            assertEquals(headers.size, requestHeaders!!.size)

            requestHeaders.forEach { (name, value) ->
                assertEquals(value, requestHeaders[name])
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ThrowHttpException {
        @ParameterizedTest
        @MethodSource("exceptionTestData")
        fun `should throw http exception when status is above 300`(testData: ExceptionTestData) {
            runBlocking(SupervisorJob()) {
                mockEngineHandler = {
                    respond(content = testData.body, status = HttpStatusCode.fromValue(testData.status))
                }

                val result = client.execute<String>(
                    method = HttpMethod.Get,
                    uri = uri
                )

                assertTrue(result.isFailure)
            }
        }

        @Suppress("unused")
        private fun exceptionTestData(): Stream<ExceptionTestData> {
            return Stream.of(
                ExceptionTestData(301, "httpbody"),
                ExceptionTestData(301, ""),
                ExceptionTestData(400, "httpbody")
            )
        }
    }

    data class ExceptionTestData(
        val status: Int,
        val body: String
    )
}