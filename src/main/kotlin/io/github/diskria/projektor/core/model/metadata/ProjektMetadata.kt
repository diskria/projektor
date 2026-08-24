package io.github.diskria.projektor.core.model.metadata

import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import kotlinx.serialization.Serializable

@Serializable
internal data class ProjektMetadata(
    val projektTypes: Set<ProjektType>,
    val repo: GithubRepo,
    val packageName: String,
    val name: String,
    val version: String,
    val license: LicenseType,
    val description: String,
    val tags: Set<String>,
)
