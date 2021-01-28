package io.supabase.postgrest.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

/**
 * Default implementation of the [PostgrestJsonConverter] used by the PostgrestDefaultClient.
 *
 * Uses Jackson FasterXML for JSON (de)-serialization.
 */
class PostgrestJsonConverterJackson : PostgrestJsonConverter {

    private val objectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun serialize(data: Any): String {
        return objectMapper.writeValueAsString(data)
    }

    override fun <T : Any> deserialize(text: String, responseType: Class<T>): T {
        return objectMapper.readValue(text, responseType)
    }
}

