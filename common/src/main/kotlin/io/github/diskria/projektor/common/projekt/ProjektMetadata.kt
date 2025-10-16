package io.github.diskria.projektor.common.projekt

import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.publishing.PublishingTargetType

data class ProjektMetadata(
    val type: ProjektType,
    val owner: String,
    val developer: String,
    val email: String,
    val repo: String,
    val name: String,
    val description: String,
    val version: String,
    val license: LicenseType,
    val publishingTarget: PublishingTargetType,
    val tags: Set<String>,
)
