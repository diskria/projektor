package io.github.diskria.projektor.common.projekt.metadata

import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.common.publishing.PublishingTargetType

data class ProjektMetadata(
    val repository: GithubRepository,
    val email: String,
    val name: String,
    val description: String,
    val version: String,
    val license: LicenseType,
    val publishingTarget: PublishingTargetType,
    val tags: Set<String>,
)
