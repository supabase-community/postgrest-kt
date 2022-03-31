package io.supabase.postgrest.http

/**
 * PostgREST provides the count in the content-range header, if a prefer header is sent requesting the count information.
 */
fun extractCount(responseHeaders: Map<String, List<String>>, requestHeaders: Map<String, List<String>>): Long? {
    val preferHeader = requestHeaders.entries.firstOrNull { it.key.lowercase() == "prefer" }?.value

    val countHeader = preferHeader != null && preferHeader.find {
        it.matches(Regex("count=(exact|planned|estimated)"))
    } != null

    return if (countHeader) {
        val contentRange =
            responseHeaders.entries.firstOrNull { it.key.lowercase() == "content-range" }?.value?.firstOrNull()
                ?.split("/")
        if (contentRange != null && contentRange.size > 1) {
            contentRange[1].toLong()
        } else {
            null
        }
    } else {
        null
    }
}