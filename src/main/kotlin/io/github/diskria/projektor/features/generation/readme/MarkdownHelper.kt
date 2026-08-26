package io.github.diskria.projektor.features.generation.readme

import io.ktor.http.*

internal object MarkdownHelper {

    const val SEPARATOR: String = "\n\n---\n\n"

    fun fileName(name: String): String = "${name.uppercase()}.md"

    fun header(text: String, level: Int): String {
        require(level in 1..6) { "Header level must be between 1 and 6" }
        return "${"#".repeat(level)} $text\n\n"
    }

    fun link(url: String, content: String): String = "[$content]($url)"

    fun image(url: Url, alt: String): String = "![$alt]($url)"
}
