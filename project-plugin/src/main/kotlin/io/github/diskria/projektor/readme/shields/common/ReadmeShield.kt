package io.github.diskria.projektor.readme.shields.common

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.readme.MarkdownHelper
import io.ktor.http.*

abstract class ReadmeShield() {

    abstract fun getLabel(): String

    abstract fun getUrl(): Url

    abstract fun getAlt(): String

    abstract fun getPathSegments(): List<String>

    open fun getStyle(): ShieldStyle = ShieldStyle.FOR_THE_BADGE

    open fun getParameters(): List<Property<String>> = emptyList()

    fun buildMarkdown(): String {
        val shieldUrl = buildUrl("img.shields.io") {
            path(*getPathSegments().toTypedArray())
            val totalParameters = getCommonParameters() + getParameters()
            totalParameters.forEach {
                parameters.append(it.name, it.value)
            }
        }
        return MarkdownHelper.link(getUrl(), MarkdownHelper.image(shieldUrl, getAlt()))
    }

    private fun getCommonParameters(): List<Property<String>> {
        val label by getLabel().autoNamedProperty()
        val style by getStyle().getParameterName().autoNamedProperty()
        return listOf(label, style)
    }

    companion object {
        protected const val LATEST_VERSION_PATH_SEGMENT: String = "v"
    }
}
