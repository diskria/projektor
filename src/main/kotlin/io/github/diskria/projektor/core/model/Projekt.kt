package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.api.GradlePluginDsl
import io.github.diskria.projektor.api.KotlinLibraryDsl
import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.projektMetadata
import io.github.diskria.projektor.internal.utils.capitalized
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal sealed interface Projekt {

    val metadata: ProjektMetadata
    val name: String
    val packageName: String get() = "${metadata.namespace}.${name.lowercase().replace("-", "_")}"
    val javaVersion: Int get() = 25
    val jvmTarget: Int get() = 17
    val classNamePrefix: String get() = name.split("-").joinToString("") { it.capitalized() }

    interface Regular : Projekt {
        val version: String
        val description: String
        val license: License?
        val softwareComponent: String? get() = null
        val distributionTargetTypes: List<DistributionTargetType>
        val isSourcesEnabled: Boolean get() = true
        val isJavadocEnabled: Boolean get() = true
        val displayName: String get() = metadata.displayName

        override val metadata: ProjektMetadata.Regular
    }

    interface BuildLogic : Projekt {
        override val metadata: ProjektMetadata.BuildLogic
    }
}

internal sealed interface KotlinLibrary : Projekt {

    class Regular(
        override val metadata: ProjektMetadata.Regular,
        override val distributionTargetTypes: List<DistributionTargetType>,
        private val configuration: KotlinLibraryDsl,
    ) : KotlinLibrary, Projekt.Regular {
        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.license?.mapToModel()
        override val name: String get() = configuration.name.orNull ?: metadata.repo.name
        override val description: String get() = configuration.description.orNull ?: metadata.about.description
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val javaVersion: Int get() = configuration.javaVersion.orNull ?: super<Projekt.Regular>.javaVersion
        override val jvmTarget: Int get() = configuration.jvmTarget.orNull ?: super<Projekt.Regular>.jvmTarget
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
        fun of(project: Project, configuration: KotlinLibraryDsl): KotlinLibrary =
            when (val metadata = project.rootProject.projektMetadata) {
                is ProjektMetadata.Regular -> {
                    val extension = project.extensions.getByType<ProjektExtension>()
                    Regular(metadata, extension.distributionTargets.get(), configuration)
                }

                is ProjektMetadata.BuildLogic -> BuildLogic(metadata, configuration)
            }
    }
}

internal sealed interface GradlePlugin : Projekt {

    val id: String get() = packageName

    class Regular(
        override val metadata: ProjektMetadata.Regular,
        override val distributionTargetTypes: List<DistributionTargetType>,
        private val configuration: GradlePluginDsl,
    ) : GradlePlugin, Projekt.Regular {
        val tags: Set<String> get() = configuration.tags.orNull ?: metadata.about.tags

        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.license?.mapToModel()
        override val name: String get() = configuration.name.orNull ?: metadata.repo.name
        override val description: String get() = configuration.description.orNull ?: metadata.about.description
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val javaVersion: Int get() = configuration.javaVersion.orNull ?: super<Projekt.Regular>.javaVersion
        override val jvmTarget: Int get() = configuration.jvmTarget.orNull ?: super<Projekt.Regular>.jvmTarget
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
        fun of(project: Project, configuration: GradlePluginDsl): GradlePlugin =
            when (val metadata = project.rootProject.projektMetadata) {
                is ProjektMetadata.Regular -> {
                    val extension = project.extensions.getByType<ProjektExtension>()
                    Regular(metadata, extension.distributionTargets.get(), configuration)
                }

                is ProjektMetadata.BuildLogic -> BuildLogic(metadata, configuration)
            }
    }
}
