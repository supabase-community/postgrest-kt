package io.supabase.postgrest.builder

/**
 * Remove whitespaces except when quoted.
 */
fun cleanColumns(columns: String): String {
    var quoted = false

    return columns
            .toCharArray()
            .map { character ->
                if (character.isWhitespace() && !quoted) {
                    return@map ""
                }
                if (character == '"') {
                    quoted = !quoted
                }

                return@map character
            }.joinToString("")
}