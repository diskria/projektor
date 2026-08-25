package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.PublishingTargetType.MAVEN_CENTRAL

internal class MavenCentralShield(projekt: Projekt) : PublishingTargetShield(MAVEN_CENTRAL, projekt) {

    override fun getPathSegments(): List<String> = listOf(
        target.id,
        LATEST_VERSION_PATH_SEGMENT,
        projekt.metadata.repo.owner.namespace,
        "${projekt.metadata.repo.name}.svg",
    )
}
