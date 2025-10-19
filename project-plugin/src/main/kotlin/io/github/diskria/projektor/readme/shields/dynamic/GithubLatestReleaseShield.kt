package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import io.github.diskria.projektor.common.repository.RepositoryHost

open class GithubLatestReleaseShield(
    metadata: ProjektMetadataExtra
) : DynamicShield(metadata, listOf("sort" to "semver")) {

    override fun getPathParts(): List<String> {
        val repository = metadata.repository
        return listOf(RepositoryHost.GITHUB.shortName, "v", "tag", repository.owner.name, repository.name)
    }
}
