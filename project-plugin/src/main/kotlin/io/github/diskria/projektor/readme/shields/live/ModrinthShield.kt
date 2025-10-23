package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType.MODRINTH

class ModrinthShield(metadata: ProjektMetadata) : PublishingTargetShield(MODRINTH, metadata) {

    override fun getPathSegments(): List<String> =
        listOf(
            target.getName(`kebab-case`),
            LATEST_VERSION_PATH_SEGMENT,
            metadata.repo.name
        )
}
