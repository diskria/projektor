package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.PublishingTargetType.GRADLE_PLUGIN_PORTAL

internal class GradlePluginPortalShield(projekt: Projekt) : PublishingTargetShield(GRADLE_PLUGIN_PORTAL, projekt) {

    override fun getPathSegments(): List<String> = listOf(
        target.id,
        LATEST_VERSION_PATH_SEGMENT,
        "${projekt.packageName}.svg",
    )
}
