package io.github.diskria.projektor.features.generation.readme.shields.common

import io.github.diskria.projektor.features.generation.readme.MarkdownHelper
import io.ktor.http.*

internal abstract class ReadmeShield {

    abstract fun getLabel(): String

    abstract fun getUrl(): String

    abstract fun getAlt(): String

    abstract fun getPathSegments(): List<String>

    open fun getStyle(): ShieldStyle = ShieldStyle.FOR_THE_BADGE

    open fun getParameters(): List<Pair<String, String>> = emptyList()

    fun buildMarkdown(): String {
        val shieldUrl = URLBuilder("https://img.shields.io").apply {
            getPathSegments().forEach { segment ->
                pathSegments = pathSegments + segment
            }

            val totalParameters = getCommonParameters() + getParameters()
            totalParameters.forEach { (key, value) ->
                parameters.append(key, value)
            }
        }.build()

        return MarkdownHelper.link(getUrl(), MarkdownHelper.image(shieldUrl, getAlt()))
    }

    private fun getCommonParameters(): List<Pair<String, String>> = listOf(
        "label" to getLabel(),
        "style" to getStyle().parameterName,
    )

    companion object {
        const val LATEST_VERSION_PATH_SEGMENT: String = "v"
    }
}
