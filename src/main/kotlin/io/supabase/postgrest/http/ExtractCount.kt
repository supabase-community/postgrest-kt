package io.supabase.postgrest.http

import java.util.Locale

/**
 * PostgREST provides the count in the content-range header, if a prefer header is sent requesting the count information.
 */
fun extractCount(responseHeaders: Map<String, String>, requestHeaders: Map<String, String>): Long? {
    val preferHeader = requestHeaders.entries.firstOrNull { it.key.lowercase(Locale.getDefault()) == "prefer" }?.value

    val countHeader = preferHeader != null && preferHeader.matches(Regex("count=(exact|planned|estimated)"))

    return if (countHeader) {
        val contentRange = responseHeaders.entries.firstOrNull { it.key.lowercase(Locale.getDefault()) == "content-range" }?.value?.split("/")
        if (contentRange != null && contentRange.size > 1) {
            contentRange[1].toLong()
        } else {
            null
        }
    } else {
        null
    }
}