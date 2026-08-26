package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt

internal abstract class GithubLatestTagShield(
    target: DistributionTargetType,
    projekt: Projekt,
) : DistributionTargetShield(target, projekt) {

    override fun getPathSegments(): List<String> =
        listOf(
            "github",
            LATEST_VERSION_PATH_SEGMENT,
            "tag",
            projekt.metadata.repo.owner.name,
            "${projekt.metadata.repo.name}.svg",
        )

    override fun getParameters(): List<Pair<String, String>> =
        listOf("sort" to "semver")
}
