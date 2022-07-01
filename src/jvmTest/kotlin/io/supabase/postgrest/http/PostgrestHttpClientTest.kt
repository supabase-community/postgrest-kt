package io.supabase.postgrest.http


import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(DelicateCoroutinesApi::class)
@ExperimentalCoroutinesApi
class PostgrestHttpClientTest {

    private var mockEngineHandler: (MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData)? = null

    private val uri = Url("https://test.com")
    private val mockEngine = MockEngine { request ->
        mockEngineHandler!!(request)
    }

    private lateinit var httpClient: HttpClient

    private lateinit var client: PostgrestHttpClient

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @BeforeTest
    fun initTest() {
        Dispatchers.setMain(mainThreadSurrogate)

        httpClient = HttpClient(mockEngine) {
            expectSuccess = false
            followRedirects = false
            install(Logging)
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }

        client = PostgrestHttpClient(httpClient)
    }


    @Test
    fun `should set http headers`() = runTest {

        val headers = buildHeaders {
            append("Authorization", "foobar")
            append("Content-Type", "application/json")
        }

        mockEngineHandler = {
            respond(content = "", headers = headersOf())
        }
        client.execute<Any>(
            uri = uri,
            method = HttpMethod.Get,
            headers = headers
        )

        val request = mockEngine.requestHistory.firstOrNull()
        val requestHeaders = request?.headers?.toMap()

        //TODO CHANGE TEST to Accept Charset
        assertEquals(headers.toMap().size, (requestHeaders!!.toMutableMap() - "Accept-Charset").size)

        requestHeaders.forEach { (name, value) ->
            assertEquals(value, requestHeaders[name])
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ThrowHttpException {
        @ParameterizedTest
        @MethodSource("exceptionTestData")
        fun `should throw http exception when status is above 300`(testData: ExceptionTestData) = runTest {
            mockEngineHandler = {
                respond(content = testData.body, status = HttpStatusCode.fromValue(testData.status))
            }

            val result = client.execute<String>(
                method = HttpMethod.Get,
                uri = uri
            )

            println("Result ${result.isFailure}")

            assertTrue(result.isFailure)
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SerializeDeserialize {

        @Test
        fun `serialize and deserialize data successfully with Json Header`() = runTest {
            val testString = """{ "title" : "title", "description": "description" }"""

            mockEngineHandler = {
                respond(
                    content = testString, status = HttpStatusCode.OK, headers = headersOf(
                        HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
                    )
                )
            }

            val result = client.execute<TestClassData>(
                method = HttpMethod.Get,
                uri = uri
            )

            assertTrue(result.isSuccess)

            assertEquals(
                result.getOrThrow().body,
                TestClassData(
                    title = "title",
                    description = "description"
                )
            )
        }

        @Test
        fun `serialize and deserialize data failed without Json Header`() = runTest {

            val testString = """{ "title" : "title", "description": "description" }"""

            mockEngineHandler = {
                respond(
                    content = testString, status = HttpStatusCode.OK, headers = headersOf()
                )
            }

            val result = client.execute<TestClassData>(
                method = HttpMethod.Get,
                uri = uri
            )

            assertTrue(result.isFailure)

        }
    }

    data class ExceptionTestData(
        val status: Int,
        val body: String
    )

    @kotlinx.serialization.Serializable
    data class TestClassData(
        val title: String,
        val description: String
    )
}