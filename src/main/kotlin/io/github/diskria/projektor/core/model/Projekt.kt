package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.api.GradlePluginDsl
import io.github.diskria.projektor.api.KotlinLibraryDsl
import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.capitalized
import io.github.diskria.projektor.extensions.get
import org.gradle.api.Project

sealed interface Projekt {

    val name: String
    val metadata: ProjektMetadata
    val javaVersion: Int get() = ToolchainDefaults.JAVA_VERSION
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

sealed interface KotlinLibrary : Projekt {

    class Distributable(
        override val name: String,
        override val metadata: ProjektMetadata.Distributable,
        override val distributionTargetTypes: List<DistributionTargetType>,
        internal val configuration: KotlinLibraryDsl,
    ) : KotlinLibrary, Projekt.Distributable {
        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.licenseType?.mapToModel()
        override val description: String get() = configuration.description.orNull ?: metadata.about.description
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val javaVersion: Int
            get() = configuration.javaVersion.orNull ?: super<Projekt.Distributable>.javaVersion
        override val jvmTarget: Int
            get() = configuration.jvmTarget.orNull ?: super<Projekt.Distributable>.jvmTarget
    }

    class BuildLogic(
        override val name: String,
        override val metadata: ProjektMetadata.BuildLogic,
        internal val configuration: KotlinLibraryDsl,
    ) : KotlinLibrary, Projekt.BuildLogic {
        override val javaVersion: Int get() = configuration.javaVersion.orNull ?: super<Projekt.BuildLogic>.javaVersion
        override val jvmTarget: Int get() = configuration.jvmTarget.orNull ?: super<Projekt.BuildLogic>.jvmTarget
    }

    companion object {
        fun of(project: Project, projektMetadata: ProjektMetadata, configuration: KotlinLibraryDsl): KotlinLibrary {
            val module = projektMetadata.getModule(project, ProjektType.KOTLIN_LIBRARY)
            return when (projektMetadata) {
                is ProjektMetadata.Distributable -> {
                    val extension = project.extensions.get<ProjektExtension>()
                    val targets = extension.distributionTargets.orNull.orEmpty()
                    check(targets.isNotEmpty()) {
                        "Distributable projekts must have at least one distribution target! " +
                            "Configure it via 'distribute { ... }'"
                    }
                    Distributable(module.name, projektMetadata, targets, configuration)
                }

                is ProjektMetadata.BuildLogic -> {
                    val extension = project.extensions.get<ProjektExtension>()
                    check(extension.distributionTargets.orNull.isNullOrEmpty()) {
                        "Build logic projekts shouldn't have distribution targets"
                    }
                    check(!configuration.description.isPresent) {
                        "Build logic projekts shouldn't have a description"
                    }
                    check(!configuration.version.isPresent) {
                        "Build logic projekts shouldn't have a version"
                    }
                    BuildLogic(module.name, projektMetadata, configuration)
                }
            }
        }
    }
}

sealed interface GradlePlugin : Projekt {

    val id: String get() = "${metadata.namespace}.${name.lowercase()}"

    class Distributable(
        override val name: String,
        override val metadata: ProjektMetadata.Distributable,
        override val distributionTargetTypes: List<DistributionTargetType>,
        internal val configuration: GradlePluginDsl,
    ) : GradlePlugin, Projekt.Distributable {
        val tags: Set<String> get() = configuration.tags.orNull?.ifEmpty { null } ?: metadata.about.tags

        override val softwareComponent: String get() = "java"
        override val license: License? = metadata.licenseType?.mapToModel()
        override val description: String get() = configuration.description.orNull ?: metadata.about.description
        override val version: String get() = configuration.version.orNull ?: metadata.version
        override val javaVersion: Int
            get() = configuration.javaVersion.orNull ?: super<Projekt.Distributable>.javaVersion
        override val jvmTarget: Int
            get() = configuration.jvmTarget.orNull ?: super<Projekt.Distributable>.jvmTarget
    }

    class BuildLogic(
        override val name: String,
        override val metadata: ProjektMetadata.BuildLogic,
        internal val configuration: GradlePluginDsl,
    ) : GradlePlugin, Projekt.BuildLogic {
        override val javaVersion: Int get() = configuration.javaVersion.orNull ?: super<Projekt.BuildLogic>.javaVersion
        override val jvmTarget: Int get() = configuration.jvmTarget.orNull ?: super<Projekt.BuildLogic>.jvmTarget
    }

    companion object {
        fun of(project: Project, projektMetadata: ProjektMetadata, configuration: GradlePluginDsl): GradlePlugin {
            val module = projektMetadata.getModule(project, ProjektType.GRADLE_PLUGIN)
            return when (projektMetadata) {
                is ProjektMetadata.Distributable -> {
                    val extension = project.extensions.get<ProjektExtension>()
                    val targets = extension.distributionTargets.orNull.orEmpty()
                    check(targets.isNotEmpty()) {
                        "Distributable projekts must have at least one distribution target! " +
                            "Configure it via 'distribute { ... }'"
                    }
                    Distributable(module.name, projektMetadata, targets, configuration)
                }

                is ProjektMetadata.BuildLogic -> {
                    val extension = project.extensions.get<ProjektExtension>()
                    check(extension.distributionTargets.orNull.isNullOrEmpty()) {
                        "Build logic projekts shouldn't have distribution targets"
                    }
                    check(!configuration.description.isPresent) {
                        "Build logic projekts shouldn't have a description"
                    }
                    check(configuration.tags.orNull.isNullOrEmpty()) {
                        "Build logic projekts shouldn't have tags"
                    }
                    check(!configuration.version.isPresent) {
                        "Build logic projekts shouldn't have a version"
                    }
                    BuildLogic(module.name, projektMetadata, configuration)
                }
            }
        }
    }
}
