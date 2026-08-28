package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.ProjektModule
import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubOwner
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.check
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.property
import java.io.File
import javax.inject.Inject

open class ProjektMetadataExtension @Inject internal constructor(
    private val settings: Settings,
    objects: ObjectFactory,
) : ProjektorScope {

    val version = objects.property<String>()
    val email = objects.property<String>().convention("diskria@proton.me")

    internal val modules = mutableListOf<ProjektModule>()

    internal var license: LicenseType? = null
        private set

    internal var isMonorepo: Boolean = false
        private set

    fun gradlePlugin(name: String? = null) {
        ensureSingleRepoMode()
        configureGradlePluginRepositories()
        registerModule(":", ProjektType.GRADLE_PLUGIN, name)
    }

    fun kotlinLibrary(name: String? = null) {
        ensureSingleRepoMode()
        configureKotlinLibraryRepositories()
        registerModule(":", ProjektType.KOTLIN_LIBRARY, name)
    }

    fun license(configure: LicensingDsl.() -> Unit) {
        LicensingDsl { license = it }.configure()
    }

    fun monorepo(configure: MonorepoDsl.() -> Unit) {
        Errors.frontend.check(modules.isEmpty()) {
            "Cannot configure 'monorepo { ... }' when a single-repo project type has already been declared!"
        }
        isMonorepo = true
        MonorepoDsl(settings, this).configure()
    }

    private fun ensureSingleRepoMode() {
        Errors.frontend.check(!isMonorepo) {
            "Cannot declare single-repo project types outside of existing 'monorepo { ... }' block!"
        }
        Errors.frontend.check(modules.isEmpty()) {
            "Single-repo supports only one project type! Use 'monorepo { ... }' for multiple modules."
        }
    }

    internal fun registerModule(path: String, type: ProjektType, name: String?) {
        modules.add(ProjektModule(path, type, name ?: settings.layout.rootDirectory.asFile.name))
        settings.gradle.rootProject { rootProject ->
            rootProject.project(path) { project ->
                project.afterEvaluate {
                    Errors.frontend.check(project.plugins.hasPlugin("io.github.diskria.projektor")) {
                        "Project '$path' was declared in settings.gradle.kts, " +
                            "but 'alias(convention.plugins.projektor)' plugin was not applied in its build.gradle.kts!"
                    }
                }
            }
        }
    }

    internal fun configureGradlePluginRepositories() {
        settings.dependencyResolutionManagement.repositories.apply {
            gradlePluginPortal()
            mavenCentrals()
        }
    }

    internal fun configureKotlinLibraryRepositories() {
        settings.dependencyResolutionManagement.repositories.apply {
            mavenCentrals()
        }
    }

    internal fun ensureConfigured(
        ownerName: String,
        repoName: String,
        rootDirectory: File,
        isBuildLogic: Boolean,
    ): ProjektMetadata {
        Errors.frontend.check(modules.isNotEmpty()) {
            "Projekt type is not configured in settings.gradle.kts! " +
                "Call kotlinLibrary(), gradlePlugin() or monorepo { ... }"
        }
        return if (isBuildLogic) {
            Errors.frontend.check(license == null) { "Build logic shouldn't have a license" }
            Errors.frontend.check(!version.isPresent) { "Build logic shouldn't have a version" }
            ProjektMetadata.BuildLogic(modules = modules)
        } else {
            ProjektMetadata.Distributable(
                isMonorepo = isMonorepo,
                modules = modules,
                repo = GithubRepo(GithubOwner(ownerName, email.get()), repoName),
                version = version.orNull ?: "0.1.0",
                licenseType = license,
                about = ProjektAbout.from(rootDirectory),
            )
        }
    }
}

private fun RepositoryHandler.mavenCentrals() {
    mavenCentral { it.name = "ApacheMavenCentral" }
    maven("https://repo1.maven.org/maven2") { name = "SonatypeMavenCentral" }
}
