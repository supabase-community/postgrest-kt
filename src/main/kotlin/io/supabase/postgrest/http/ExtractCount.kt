package io.supabase.postgrest.http

fun extractCount(responseHeaders: Map<String, String>, requestHeaders: Map<String, String>): Long? {
    val preferHeader = requestHeaders["Prefer"]

    val countHeader = preferHeader != null && preferHeader.matches(Regex("count=(exact|planned|estimated)"))

    return if (countHeader) {
        val contentRange = responseHeaders["content-range"]?.split("/")
        if (contentRange != null && contentRange.size > 1) {
            contentRange[1].toLong()
        } else {
            null
        }
    } else {
        null
    }
}