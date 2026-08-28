package io.github.diskria.projektor.core.model.metadata

import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.internal.utils.capitalized
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface ProjektMetadata {

    val isMonorepo: Boolean
    val projektTypes: Set<ProjektType>
    val namespace: String

    class Distributable(
        override val isMonorepo: Boolean,
        override val projektTypes: Set<ProjektType>,
        val repo: GithubRepo,
        val version: String,
        val licenseType: LicenseType?,
        val about: ProjektAbout,
    ) : ProjektMetadata {
        val displayName: String get() = repo.name.split("-").joinToString(" ") { about.fixCase(it).capitalized() }
        override val namespace: String get() = repo.owner.namespace
    }

    class BuildLogic(
        override val projektTypes: Set<ProjektType>,
    ) : ProjektMetadata {
        override val isMonorepo: Boolean get() = true
        override val namespace: String get() = "builder"
    }
}
