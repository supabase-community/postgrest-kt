package io.supabase.postgrest.builder

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CleanColumnTest {

    @ParameterizedTest
    @MethodSource("testData")
    fun `should clean column`(testData: CleanColumnTestData) {


        val cleanedColumns = cleanColumns(testData.columns)
        assertEquals(cleanedColumns, testData.expectedColumns)
    }

    @Suppress("unused")
    private fun testData(): Stream<CleanColumnTestData> {
        return Stream.of(
            CleanColumnTestData(
                columns = "firstname,lastname",
                expectedColumns = "firstname,lastname"
            ),
            CleanColumnTestData(
                columns = "firstname ,last name",
                expectedColumns = "firstname,lastname"
            ),
            CleanColumnTestData(
                columns = "firstname,\"last name\"",
                expectedColumns = "firstname,\"last name\""
            ),
            CleanColumnTestData(
                columns = "firstname,\"last name\", sur name",
                expectedColumns = "firstname,\"last name\",surname"
            ),
        )
    }

}

data class CleanColumnTestData(
    val columns: String,
    val expectedColumns: String
)