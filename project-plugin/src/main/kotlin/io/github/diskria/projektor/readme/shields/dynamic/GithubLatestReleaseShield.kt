package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.repo.RepoHost

open class GithubLatestReleaseShield(metadata: ProjektMetadata) : DynamicShield(metadata, listOf("sort" to "semver")) {

    override fun getPathParts(): List<String> =
        listOf(RepoHost.GITHUB.shortName, "v", "tag", metadata.repo.owner.name, metadata.repo.name)
}
