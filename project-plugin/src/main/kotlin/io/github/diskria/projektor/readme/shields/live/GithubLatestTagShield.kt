package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.common.repo.RepoHost

abstract class GithubLatestTagShield(
    target: PublishingTargetType, metadata: ProjektMetadata
) : PublishingTargetShield(target, metadata) {

    override fun getPathSegments(): List<String> =
        listOf(
            RepoHost.GITHUB.shortName,
            LATEST_VERSION_PATH_SEGMENT,
            "tag",
            metadata.repo.owner.name,
            fileName(metadata.repo.name, Constants.File.Extension.SVG)
        )

    override fun getParameters(): List<Property<String>> {
        val sort by "semver".autoNamedProperty()
        return listOf(sort)
    }
}
