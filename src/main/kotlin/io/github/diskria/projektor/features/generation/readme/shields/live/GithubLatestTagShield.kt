package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.PublishingTargetType
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata

internal abstract class GithubLatestTagShield(
    target: PublishingTargetType,
    metadata: ProjektMetadata
) : PublishingTargetShield(target, metadata) {

    override fun getPathSegments(): List<String> =
        listOf(
            "github",
            LATEST_VERSION_PATH_SEGMENT,
            "tag",
            metadata.repo.owner.name,
            "${metadata.repo.name}.svg"
        )

    override fun getParameters(): List<Pair<String, String>> =
        listOf("sort" to "semver")
}
