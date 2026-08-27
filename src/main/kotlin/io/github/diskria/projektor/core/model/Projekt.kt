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
    val packageName: String get() = metadata.packageName
    val displayName: String get() = metadata.displayName
    val javaVersion: Int get() = 25
    val jvmTarget: Int get() = 17
    val classNamePrefix: String get() = metadata.repo.name.split("-").joinToString("") { it.capitalized() }

    interface Regular : Projekt {
        override val metadata: ProjektMetadata.Regular
        val version: String
        val description: String?
        val tags: Set<String>?
        val license: License?
        val softwareComponent: String? get() = null
        val distributionTargetTypes: List<DistributionTargetType>
        val isSourcesEnabled: Boolean get() = true
        val isJavadocEnabled: Boolean get() = true
    }

    interface BuildLogic : Projekt {
        override val metadata: ProjektMetadata.BuildLogic
    }
}

internal sealed interface KotlinLibrary : Projekt {
    val configuration: KotlinLibraryDsl

    class Regular(
        override val metadata: ProjektMetadata.Regular,
        override val distributionTargetTypes: List<DistributionTargetType>,
        override val configuration: KotlinLibraryDsl,
    ) : KotlinLibrary, Projekt.Regular {
        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.license?.mapToModel()
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val description: String get() = metadata.about.description
        override val tags: Set<String> get() = metadata.about.tags
        override val javaVersion: Int get() = configuration.javaVersion.getOrElse(super<Projekt.Regular>.javaVersion)
        override val jvmTarget: Int get() = configuration.jvmTarget.getOrElse(super<Projekt.Regular>.jvmTarget)
    }

    class BuildLogic(
        override val metadata: ProjektMetadata.BuildLogic,
        override val configuration: KotlinLibraryDsl,
    ) : KotlinLibrary, Projekt.BuildLogic {
        override val javaVersion: Int get() = configuration.javaVersion.getOrElse(super<Projekt.BuildLogic>.javaVersion)
        override val jvmTarget: Int get() = configuration.jvmTarget.getOrElse(super<Projekt.BuildLogic>.jvmTarget)
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
    val configuration: GradlePluginDsl

    class Regular(
        override val metadata: ProjektMetadata.Regular,
        override val distributionTargetTypes: List<DistributionTargetType>,
        override val configuration: GradlePluginDsl,
    ) : GradlePlugin, Projekt.Regular {
        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.license?.mapToModel()
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val description: String get() = metadata.about.description
        override val tags: Set<String> get() = metadata.about.tags
        override val javaVersion: Int get() = configuration.javaVersion.getOrElse(super<Projekt.Regular>.javaVersion)
        override val jvmTarget: Int get() = configuration.jvmTarget.getOrElse(super<Projekt.Regular>.jvmTarget)
    }

    class BuildLogic(
        override val metadata: ProjektMetadata.BuildLogic,
        override val configuration: GradlePluginDsl,
    ) : GradlePlugin, Projekt.BuildLogic {
        override val javaVersion: Int get() = configuration.javaVersion.getOrElse(super<Projekt.BuildLogic>.javaVersion)
        override val jvmTarget: Int get() = configuration.jvmTarget.getOrElse(super<Projekt.BuildLogic>.jvmTarget)
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
