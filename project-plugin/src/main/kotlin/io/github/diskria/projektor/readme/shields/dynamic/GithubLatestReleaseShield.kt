package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.common.repository.RepositoryHost
import io.github.diskria.projektor.publishing.common.PublishingTarget

open class GithubLatestReleaseShield(
    repository: GithubRepository,
    publishingTarget: PublishingTarget,
    url: String
) : DynamicShield(
    pathParts = listOf(RepositoryHost.GITHUB.shortName, "v", "tag", repository.owner.name, repository.name),
    extraParameters = listOf("sort" to "semver"),
    publishingTarget = publishingTarget,
    url = url,
)
