package io.github.diskria.projektor.projekt

import io.github.diskria.utils.kotlin.extensions.mappers.toName
import io.github.diskria.utils.kotlin.words.PascalCase

enum class PublishingTarget {
    GITHUB_PACKAGES,
    GITHUB_PAGES,
    MAVEN_CENTRAL,
    MODRINTH,
    GRADLE_PLUGIN_PORTAL,
    GOOGLE_PLAY,
}

fun PublishingTarget.mavenName(): String =
    toName(PascalCase)
