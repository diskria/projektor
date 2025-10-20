package io.github.diskria.projektor.common.projekt.metadata

import io.github.diskria.projektor.common.github.GithubRepo
import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.publishing.PublishingTargetType

data class ProjektMetadata(
    val type: ProjektType,
    val repo: GithubRepo,
    val packageNameBase: String,
    val name: String,
    val version: String,
    val license: LicenseType,
    val publishingTarget: PublishingTargetType,
    val description: String,
    val tags: Set<String>,
)
