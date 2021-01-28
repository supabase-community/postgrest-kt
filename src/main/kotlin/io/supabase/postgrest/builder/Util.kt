package io.supabase.postgrest.builder

/**
 * Remove whitespaces except when quoted
 */
fun cleanColumns(columns: String): String {
    var quoted = false

    return columns
            .split("")
            .map { character ->
                if (character.matches(Regex("\\s")) && !quoted) {
                    return@map ""
                }
                if (character == "\"") {
                    quoted = !quoted
                }

                return@map character
            }.joinToString("")
}