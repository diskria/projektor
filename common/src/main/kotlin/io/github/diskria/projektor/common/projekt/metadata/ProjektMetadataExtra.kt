package io.github.diskria.projektor.common.projekt.metadata

import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.common.publishing.PublishingTargetType

data class ProjektMetadataExtra(
    val type: ProjektType,
    val repository: GithubRepository,
    val packageName: String,
    val name: String,
    val version: String,
    val license: LicenseType,
    val publishingTarget: PublishingTargetType,
    val description: String,
    val tags: Set<String>,
)
