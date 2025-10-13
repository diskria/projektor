package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.appendSuffix
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
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
            extraParameters.forEach { (name, value) ->
                parameters.append(name, value.orEmpty())
            }
        }

    override fun getAlt(): String =
        label
}
