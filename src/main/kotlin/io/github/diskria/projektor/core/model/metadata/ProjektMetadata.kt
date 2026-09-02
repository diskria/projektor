package io.github.diskria.projektor.core.model.metadata

import io.github.diskria.projektor.core.model.ProjektModule
import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.extensions.capitalized
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.Serializable as PropertySerializable

sealed interface ProjektMetadata : PropertySerializable {

    val modules: List<ProjektModule>
    val namespace: String

    fun getModule(project: Project, type: ProjektType): ProjektModule =
        checkNotNull(modules.find { it.path == project.path && it.type == type }) {
            "Module '${project.path}' is not registered as a '${type.id}' in projekt dsl."
        }

    class Distributable(
        val isMonorepo: Boolean,
        val repo: GithubRepo,
        val version: String,
        val licenseType: LicenseType?,
        val about: ProjektAbout,
        override val modules: List<ProjektModule>,
    ) : ProjektMetadata {
        val displayName: String get() = repo.name.split("-").joinToString(" ") { about.fixCase(it).capitalized() }
        override val namespace: String get() = repo.owner.namespace
    }

    class BuildLogic(
        override val modules: List<ProjektModule>,
    ) : ProjektMetadata {
        override val namespace: String get() = "builder"
    }
}

interface ProjektMetadataBuildService : BuildService<ProjektMetadataBuildService.Parameters> {

    val projektMetadata: Property<ProjektMetadata> get() = parameters.projektMetadata

    interface Parameters : BuildServiceParameters {
        val projektMetadata: Property<ProjektMetadata>
    }
}
