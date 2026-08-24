package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.PublishingTargetType.GRADLE_PLUGIN_PORTAL
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata

internal class GradlePluginPortalShield(
    metadata: ProjektMetadata
) : PublishingTargetShield(GRADLE_PLUGIN_PORTAL, metadata) {

    override fun getPathSegments(): List<String> = listOf(
        target.id,
        LATEST_VERSION_PATH_SEGMENT,
        "${metadata.packageName}.svg"
    )
}
