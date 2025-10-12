package io.github.diskria.projektor.readme.shields

import io.github.diskria.kotlin.utils.extensions.appendSuffix
import io.ktor.http.*

sealed class DynamicShield(
    val pathParts: List<String>,
    val extraParameters: List<Pair<String, String?>> = emptyList(),
    label: String,
    url: String
) : ReadmeShield(label, url) {

    override val urlBuilder: URLBuilder.() -> Unit
        get() = {
            val segments = pathParts.toTypedArray()
            segments[segments.lastIndex] = segments.last().appendSuffix(".svg")
            path(*segments)
            parameters.apply {
                extraParameters.forEach { (name, value) ->
                    append(name, value.orEmpty())
                }
            }
        }

    override fun getAlt(): String =
        label
}
