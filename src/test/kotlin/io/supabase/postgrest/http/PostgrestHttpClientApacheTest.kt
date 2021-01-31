package io.supabase.postgrest.http

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.supabase.postgrest.json.PostgrestJsonConverterJackson
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.core5.http.ClassicHttpRequest
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.hc.core5.http.io.entity.StringEntity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.net.URI
import java.util.stream.Stream

internal class PostgrestHttpClientApacheTest {

    private val uri = "https://test.com"
    private val httpClientMock = mockk<CloseableHttpClient>()

    private val postgrestHttpClient = PostgrestHttpClientApache(
            httpClient = { httpClientMock },
            jsonConverter = PostgrestJsonConverterJackson()
    )

    init {
        every { httpClientMock.close() }.returns(Unit)
    }

    @Test
    fun `should set http headers`() {
        val httpResponse = mockk<CloseableHttpResponse>()
        every { httpResponse.code } returns 200
        every { httpResponse.entity } returns null

        val requestCapture = mockHttpCallWithGetRequest(httpResponse)

        val headers = mapOf("Authorization" to "foobar", "Content-Type" to "application/json")

        postgrestHttpClient.execute(
                method = Method.GET,
                uri = URI(uri),
                headers = headers
        )

        val request = requestCapture.captured

        assertAll {
            assertThat(request.headers).hasSize(headers.size)
            headers.forEach { (name, value) ->
                assertThat(request.getHeader(name).value).isEqualTo(value)
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ThrowHttpException {

        @ParameterizedTest
        @MethodSource("exceptionTestData")
        fun `should throw http exception when status is above 300`(testData: ExceptionTestData) {
            val httpResponse = mockk<CloseableHttpResponse>()
            val responseCode = testData.status
            val httpBody = testData.body

            every { httpResponse.code } returns responseCode
            every { httpResponse.entity } returns httpBody?.let { StringEntity(it) }

            mockHttpCallWithGetRequest(httpResponse)

            val exception = assertThrows<PostgrestHttpException> {
                postgrestHttpClient.execute(
                        method = Method.GET,
                        uri = URI(uri)
                )
            }

            assertThat(exception.status).isEqualTo(responseCode)
            assertThat(exception.data).isEqualTo(httpBody)
        }

        @Suppress("unused")
        private fun exceptionTestData(): Stream<ExceptionTestData> {
            return Stream.of(
                    ExceptionTestData(301, "httpbody"),
                    ExceptionTestData(301, null),
                    ExceptionTestData(400, "httpbody")
            )
        }
    }

    data class ExceptionTestData(
            val status: Int,
            val body: String?
    )

    private fun mockHttpCallWithGetRequest(httpResponse: CloseableHttpResponse): CapturingSlot<ClassicHttpRequest> {
        val slot = slot<ClassicHttpRequest>()
        every { httpClientMock.execute(capture(slot), any<HttpClientResponseHandler<Any>>()) }.answers {
            val handler = args[1] as HttpClientResponseHandler<*>
            handler.handleResponse(httpResponse)
        }

        return slot
    }
}