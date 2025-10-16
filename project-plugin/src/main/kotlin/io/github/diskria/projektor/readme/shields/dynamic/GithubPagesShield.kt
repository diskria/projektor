package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.publishing.maven.GithubPages

class GithubPagesShield(repository: GithubRepository) : GithubLatestReleaseShield(
    repository = repository,
    publishingTarget = GithubPages,
    url = repository.getPagesUrl()
)
