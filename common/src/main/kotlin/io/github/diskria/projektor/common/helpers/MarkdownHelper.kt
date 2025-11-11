package io.github.diskria.projektor.common.helpers

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.BracketsType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.primitives.repeat
import io.github.diskria.kotlin.utils.extensions.wrap
import io.github.diskria.kotlin.utils.extensions.wrapWithBrackets
import io.ktor.http.*

object MarkdownHelper {

    val SEPARATOR: String by lazy {
        Constants.Char.HYPHEN.repeat(3).wrap(Constants.Char.NEW_LINE.repeat(2))
    }

    fun fileName(name: String): String =
        io.github.diskria.kotlin.utils.extensions.common.fileName(name.uppercase(), Constants.File.Extension.MARKDOWN)

    fun header(text: String, level: Int): String =
        buildString {
            val minLevel = 1
            val maxLevel = 6
            if (level !in minLevel..maxLevel) {
                gradleError("Header level must be between $minLevel and $maxLevel")
            }
            append(Constants.Char.NUMBER_SIGN.repeat(level))
            append(Constants.Char.SPACE)
            append(text)
            appendLine()
            appendLine()
        }

    fun link(url: Url, text: String): String =
        buildString {
            append(text.wrapWithBrackets(BracketsType.SQUARE))
            append(url.toString().wrapWithBrackets(BracketsType.ROUND))
        }

    fun image(url: Url, alt: String): String =
        Constants.Char.EXCLAMATION_MARK + link(url, alt)
}
