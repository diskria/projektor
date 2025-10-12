package io.github.diskria.projektor.readme.shields

import io.github.diskria.kotlin.utils.Constants
import io.ktor.http.*

sealed class StaticShield(
    label: String,
    val message: String,
    val color: String,
    url: String,
) : ReadmeShield(label, url) {

    override val urlBuilder: URLBuilder.() -> Unit
        get() = {
            path("static", "v1")
            parameters.apply {
                append("message", message)
                append("color", color)
            }
        }

    override fun getAlt(): String =
        buildString {
            append(label)
            append(Constants.Char.COLON)
            append(Constants.Char.SPACE)
            append(message)
        }
}
