package io.github.diskria.projektor.readme.shields.static

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.readme.shields.common.ReadmeShield

sealed class StaticShield(val message: String, val color: String) : ReadmeShield() {

    override fun getAlt(): String =
        buildString {
            append(getLabel())
            append(Constants.Char.COLON)
            append(Constants.Char.SPACE)
            append(message)
        }

    override fun getPathSegments(): List<String> =
        listOf("static", "v1")

    override fun getParameters(): List<Property<String>> {
        val message by message.autoNamedProperty()
        val color by color.autoNamedProperty()
        return listOf(message, color)
    }
}
