package io.supabase.postgrest.json

/**
 * Interface used by the PostgrestClient, allows replacing the default JSON converter.
 *
 * Overwrite it to replace the default Jackson FasterXML implementation.
 */
interface PostgrestJsonConverter {

    /**
     * Serializes [data] as JSON string.
     *
     * @param[data] the data to serialize
     *
     * @return JSON string
     */
    fun serialize(data: Any): String

    /**
     * Deserializes a JSON [text] to the corresponding [responseType].
     *
     * @param[text] The JSON text to convert
     * @param[responseType] The response type as Java class
     */
    fun <T : Any> deserialize(text: String, responseType: Class<T>): T
}

inline fun <reified T : Any> PostgrestJsonConverter.deserialize(content: String): T = deserialize(content, T::class.java)