package io.supabase.postgrest.json

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostgrestJsonConverterJackonTest {

    private val converter = PostgrestJsonConverterJackson()

    @ParameterizedTest
    @MethodSource("serializeData")
    fun `should serialize and deserialize`(data: Any) {
        val serialized = converter.serialize(data)

        val deserialized = converter.deserialize(serialized, data.javaClass)

        assertThat(deserialized).isEqualTo(data)
    }

    @Suppress("unused")
    private fun serializeData(): Stream<Any> {
        return Stream.of(
                "5",
                mapOf("foo" to "bar", "number" to 5),
                ConverterTestDto("bar", 5)
        )
    }
}

data class ConverterTestDto(
        val prop: String,
        val otherProp: Int
)