package io.github.diskria.projektor.features.generation.readme.shields.static

import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield

internal sealed class StaticShield(val message: String, val color: String) : ReadmeShield() {

    override fun getAlt(): String =
        buildString {
            append(getLabel())
            append(": ")
            append(message)
        }

    override fun getPathSegments(): List<String> =
        listOf("static", "v1")

    override fun getParameters(): List<Pair<String, String>> =
        listOf(
            "message" to message,
            "color" to color,
        )
}
