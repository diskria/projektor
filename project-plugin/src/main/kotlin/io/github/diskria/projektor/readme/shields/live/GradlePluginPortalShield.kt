package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GRADLE_PLUGIN_PORTAL

class GradlePluginPortalShield(metadata: ProjektMetadata) : PublishingTargetShield(GRADLE_PLUGIN_PORTAL, metadata) {

    override fun getPathSegments(): List<String> =
        listOf(
            target.getName(`kebab-case`),
            LATEST_VERSION_PATH_SEGMENT,
            fileName(metadata.packageNameBase, Constants.File.Extension.SVG)
        )
}
