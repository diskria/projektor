package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.PublishingTargetType.MAVEN_CENTRAL
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata

internal class MavenCentralShield(metadata: ProjektMetadata) : PublishingTargetShield(MAVEN_CENTRAL, metadata) {

    override fun getPathSegments(): List<String> = listOf(
        target.id,
        LATEST_VERSION_PATH_SEGMENT,
        metadata.repo.owner.namespace,
        "${metadata.repo.name}.svg"
    )
}
