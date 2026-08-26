package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.DistributionTargetType.GRADLE_PLUGIN_PORTAL
import io.github.diskria.projektor.core.model.Projekt

internal class GradlePluginPortalShield(projekt: Projekt) : DistributionTargetShield(GRADLE_PLUGIN_PORTAL, projekt) {

    override fun getPathSegments(): List<String> = listOf(
        target.id,
        LATEST_VERSION_PATH_SEGMENT,
        "${projekt.packageName}.svg",
    )
}
