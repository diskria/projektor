package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.DistributionTargetType.MAVEN_CENTRAL
import io.github.diskria.projektor.core.model.Projekt

internal class MavenCentralShield(projekt: Projekt) : DistributionTargetShield(MAVEN_CENTRAL, projekt) {

    override fun getPathSegments(): List<String> = listOf(
        target.id,
        LATEST_VERSION_PATH_SEGMENT,
        projekt.metadata.repo.owner.namespace,
        "${projekt.metadata.repo.name}.svg",
    )
}
