package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType.MAVEN_CENTRAL

class MavenCentralShield(metadata: ProjektMetadata) : PublishingTargetShield(MAVEN_CENTRAL, metadata) {

    override fun getPathSegments(): List<String> =
        listOf(
            target.getName(`kebab-case`),
            LATEST_VERSION_PATH_SEGMENT,
            metadata.repo.owner.namespace,
            fileName(metadata.repo.name, Constants.File.Extension.SVG)
        )
}
