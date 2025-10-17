package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.common.repository.RepositoryHost
import io.github.diskria.projektor.publishing.maven.GithubPackages
import io.ktor.http.*

class GithubPackagesShield(repository: GithubRepository) : GithubLatestReleaseShield(
    repository = repository,
    publishingTarget = GithubPackages,
    url = buildUrl(RepositoryHost.GITHUB.hostName) {
        path(repository.owner.name, repository.name, "packages", "latest")
    },
)
