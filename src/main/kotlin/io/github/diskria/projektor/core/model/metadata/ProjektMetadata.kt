package io.github.diskria.projektor.core.model.metadata

import io.github.diskria.projektor.core.model.ProjektModule
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.internal.utils.capitalized
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface ProjektMetadata {

    val isMonorepo: Boolean
    val modules: List<ProjektModule>
    val namespace: String

    class Distributable(
        override val isMonorepo: Boolean,
        override val modules: List<ProjektModule>,
        val repo: GithubRepo,
        val version: String,
        val licenseType: LicenseType?,
        val about: ProjektAbout,
    ) : ProjektMetadata {
        val displayName: String get() = repo.name.split("-").joinToString(" ") { about.fixCase(it).capitalized() }
        override val namespace: String get() = repo.owner.namespace
    }

    class BuildLogic(
        override val modules: List<ProjektModule>,
    ) : ProjektMetadata {
        override val isMonorepo: Boolean get() = true
        override val namespace: String get() = "builder"
    }
}
