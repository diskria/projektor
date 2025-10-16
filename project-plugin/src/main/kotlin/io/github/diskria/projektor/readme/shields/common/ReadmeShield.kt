package io.github.diskria.projektor.readme.shields.common

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.readme.MarkdownHelper
import io.ktor.http.*

abstract class ReadmeShield(
    val label: String,
    val url: String,
    val style: ShieldStyle = ShieldStyle.FOR_THE_BADGE
) {
    abstract val urlBuilder: URLBuilder.() -> Unit

    abstract fun getAlt(): String

    fun buildMarkdown(): String {
        val shieldUrl = buildUrl("img.shields.io") {
            urlBuilder()
            parameters.apply {
                append("label", label)
                append("style", style.getParameterName())
            }
        }
        return MarkdownHelper.link(url, MarkdownHelper.image(shieldUrl, getAlt()))
    }
}
