package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.api.GradlePluginDsl
import io.github.diskria.projektor.api.KotlinLibraryDsl
import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.capitalized
import io.github.diskria.projektor.internal.utils.require
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal sealed interface Projekt {

    val metadata: ProjektMetadata
    val name: String
    val javaVersion: Int get() = 25
    val jvmTarget: Int get() = 17

    val packageName: String get() = "${metadata.namespace}.${name.lowercase().replace("-", "_")}"
    val classNamePrefix: String get() = name.split("-").joinToString("") { it.capitalized() }

    interface Distributable : Projekt {
        val version: String
        val description: String
        val license: License?
        val softwareComponent: String? get() = null
        val distributionTargetTypes: List<DistributionTargetType>
        val isSourcesEnabled: Boolean get() = true
        val isJavadocEnabled: Boolean get() = true
        val displayName: String get() = metadata.displayName

        override val metadata: ProjektMetadata.Distributable
    }

    interface BuildLogic : Projekt {
        override val metadata: ProjektMetadata.BuildLogic
    }
}

internal sealed interface KotlinLibrary : Projekt {

    class Distributable(
        override val metadata: ProjektMetadata.Distributable,
        override val distributionTargetTypes: List<DistributionTargetType>,
        private val configuration: KotlinLibraryDsl,
    ) : KotlinLibrary, Projekt.Distributable {
        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.license?.mapToModel()
        override val name: String get() = configuration.name.orNull ?: metadata.repo.name
        override val description: String get() = configuration.description.orNull ?: metadata.about.description
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val javaVersion: Int
            get() = configuration.javaVersion.orNull ?: super<Projekt.Distributable>.javaVersion
        override val jvmTarget: Int
            get() = configuration.jvmTarget.orNull ?: super<Projekt.Distributable>.jvmTarget
    }

    class BuildLogic(
        override val metadata: ProjektMetadata.BuildLogic,
        private val configuration: KotlinLibraryDsl,
    ) : KotlinLibrary, Projekt.BuildLogic {
        override val name: String get() = configuration.name.orNull ?: metadata.repo.name
        override val javaVersion: Int get() = configuration.javaVersion.orNull ?: super<Projekt.BuildLogic>.javaVersion
        override val jvmTarget: Int get() = configuration.jvmTarget.orNull ?: super<Projekt.BuildLogic>.jvmTarget
    }

    companion object {
        fun of(
            project: Project,
            projektMetadata: ProjektMetadata,
            configuration: KotlinLibraryDsl,
        ): KotlinLibrary = when (projektMetadata) {
            is ProjektMetadata.Distributable -> {
                val extension = project.extensions.getByType<ProjektExtension>()
                val targets = extension.distributionTargets.orNull.orEmpty()
                Errors.frontend.require(targets.isNotEmpty()) {
                    "Distributable projekts must have at least one distribution target! Configure it via 'distribute { ... }'"
                }
                Distributable(projektMetadata, targets, configuration)
            }

            is ProjektMetadata.BuildLogic -> {
                val extension = project.extensions.getByType<ProjektExtension>()
                Errors.frontend.require(extension.distributionTargets.orNull.isNullOrEmpty()) {
                    "Build logic projekts shouldn't have distribution targets"
                }
                Errors.frontend.require(!configuration.description.isPresent) {
                    "Build logic projekts shouldn't have a description"
                }
                Errors.frontend.require(!configuration.version.isPresent) {
                    "Build logic projekts shouldn't have a version"
                }
                BuildLogic(projektMetadata, configuration)
            }
        }
    }
}

internal sealed interface GradlePlugin : Projekt {

    val id: String get() = "${metadata.namespace}.${name.lowercase()}"

    class Distributable(
        override val metadata: ProjektMetadata.Distributable,
        override val distributionTargetTypes: List<DistributionTargetType>,
        private val configuration: GradlePluginDsl,
    ) : GradlePlugin, Projekt.Distributable {
        val tags: Set<String> get() = configuration.tags.orNull ?: metadata.about.tags

        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.license?.mapToModel()
        override val name: String get() = configuration.name.orNull ?: metadata.repo.name
        override val description: String get() = configuration.description.orNull ?: metadata.about.description
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val javaVersion: Int
            get() = configuration.javaVersion.orNull ?: super<Projekt.Distributable>.javaVersion
        override val jvmTarget: Int
            get() = configuration.jvmTarget.orNull ?: super<Projekt.Distributable>.jvmTarget
    }

    class BuildLogic(
        override val metadata: ProjektMetadata.BuildLogic,
        private val configuration: GradlePluginDsl,
    ) : GradlePlugin, Projekt.BuildLogic {
        override val name: String get() = configuration.name.orNull ?: metadata.repo.name
        override val javaVersion: Int get() = configuration.javaVersion.orNull ?: super<Projekt.BuildLogic>.javaVersion
        override val jvmTarget: Int get() = configuration.jvmTarget.orNull ?: super<Projekt.BuildLogic>.jvmTarget
    }

    companion object {
        fun of(
            project: Project,
            projektMetadata: ProjektMetadata,
            configuration: GradlePluginDsl,
        ): GradlePlugin = when (projektMetadata) {
            is ProjektMetadata.Distributable -> {
                val extension = project.extensions.getByType<ProjektExtension>()
                val targets = extension.distributionTargets.orNull.orEmpty()
                Errors.frontend.require(targets.isNotEmpty()) {
                    "Distributable projekts must have at least one distribution target! Configure it via 'distribute { ... }'"
                }
                Distributable(projektMetadata, targets, configuration)
            }

            is ProjektMetadata.BuildLogic -> {
                val extension = project.extensions.getByType<ProjektExtension>()
                Errors.frontend.require(extension.distributionTargets.orNull.isNullOrEmpty()) {
                    "Build logic projekts shouldn't have distribution targets"
                }
                Errors.frontend.require(!configuration.description.isPresent) {
                    "Build logic projekts shouldn't have a description"
                }
                Errors.frontend.require(configuration.tags.orNull.isNullOrEmpty()) {
                    "Build logic projekts shouldn't have tags"
                }
                Errors.frontend.require(!configuration.version.isPresent) {
                    "Build logic projekts shouldn't have a version"
                }
                BuildLogic(projektMetadata, configuration)
            }
        }
    }
}
