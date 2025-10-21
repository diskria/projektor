package io.github.diskria.projektor.common.metadata

import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.common.repo.github.GithubRepo

data class ProjektMetadata(
    val type: ProjektType,
    val repo: GithubRepo,
    val packageNameBase: String,
    val name: String,
    val version: String,
    val license: LicenseType,
    val publishingTargets: Set<PublishingTargetType>,
    val description: String,
    val tags: Set<String>,
)
