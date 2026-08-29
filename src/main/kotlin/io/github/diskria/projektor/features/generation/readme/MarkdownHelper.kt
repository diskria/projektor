package io.github.diskria.projektor.features.generation.readme

internal object MarkdownHelper {

    const val SEPARATOR: String = "\n\n---\n\n"

    fun fileName(name: String): String = "${name.uppercase()}.md"

    fun header(text: String, level: Int): String {
        check(level in 1..6) { "Header level must be between 1 and 6" }
        return "${"#".repeat(level)} $text\n\n"
    }

    fun link(url: String, content: String): String = "[$content]($url)"

    fun image(url: String, alt: String): String = "![$alt]($url)"
}
