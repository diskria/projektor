package io.github.diskria.projektor.core.model.metadata

import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface ProjektMetadata {

    val isMonorepo: Boolean
    val projektTypes: Set<ProjektType>
    val repo: GithubRepo
    val namespace: String

    class Regular(
        override val isMonorepo: Boolean,
        override val projektTypes: Set<ProjektType>,
        override val repo: GithubRepo,
        override val namespace: String,
        val displayName: String,
        val version: String,
        val license: LicenseType?,
        val about: ProjektAbout,
    ) : ProjektMetadata

    class BuildLogic(
        override val isMonorepo: Boolean,
        override val projektTypes: Set<ProjektType>,
        override val repo: GithubRepo,
        override val namespace: String,
    ) : ProjektMetadata
}
