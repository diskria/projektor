package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubOwner
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.capitalized
import io.github.diskria.projektor.internal.utils.check
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class ProjektMetadataExtension @Inject internal constructor(
    private val settings: Settings,
    objects: ObjectFactory
) : ProjektorScope {

    val version = objects.property<String>().convention("0.1.0")
    val email = objects.property<String>().convention("diskria@proton.me")

    internal val projektTypes = mutableSetOf<ProjektType>()

    internal var license: LicenseType = LicenseType.MIT
        private set

    internal var isMonorepo: Boolean = false
        private set

    internal var primaryProjectPath: String? = null

    fun gradlePlugin() {
        ensureSingleRepoMode()
        registerProjektModule(":", ProjektType.GRADLE_PLUGIN)
        configureGradlePluginRepositories()
    }

    fun kotlinLibrary() {
        ensureSingleRepoMode()
        registerProjektModule(":", ProjektType.KOTLIN_LIBRARY)
        configureKotlinLibraryRepositories()
    }

    fun licensing(configure: LicensingDsl.() -> Unit) {
        LicensingDsl { license = it }.configure()
    }

    fun monorepo(configure: MonorepoDsl.() -> Unit) {
        Errors.frontend.check(projektTypes.isEmpty()) {
            "Cannot configure 'monorepo { ... }' when a single-repo project type has already been declared!"
        }
        isMonorepo = true
        MonorepoDsl(settings, this).configure()
    }

    private fun ensureSingleRepoMode() {
        Errors.frontend.check(!isMonorepo) {
            "Cannot declare single-repo project types outside of existing 'monorepo { ... }' block!"
        }
        Errors.frontend.check(projektTypes.isEmpty()) {
            "Single-repo supports only one project type! Use 'monorepo { ... }' for multiple modules."
        }
    }

    internal fun ensureConfigured(ownerName: String, repoName: String, about: ProjektAbout): ProjektMetadata {
        Errors.frontend.check(projektTypes.isNotEmpty()) {
            "Projekt type is not configured in settings.gradle.kts! " +
                "Call kotlinLibrary(), gradlePlugin() or monorepo { ... }"
        }
        val repo = GithubRepo(GithubOwner(ownerName, email.get()), repoName)
        return ProjektMetadata(
            projektTypes = projektTypes,
            repo = repo,
            packageName = "${repo.owner.namespace}.${repo.name.lowercase().replace("-", "_")}",
            displayName = repo.name.split("-").joinToString(" ") { about.fixCase(it).capitalized() },
            version = version.get(),
            license = license,
            description = about.description,
            tags = about.tags,
        )
    }

    internal fun registerProjektModule(path: String, type: ProjektType) {
        if (primaryProjectPath == null) {
            primaryProjectPath = path
        }
        projektTypes.add(type)

        settings.gradle.rootProject { rootProject ->
            rootProject.project(path) { project ->
                project.afterEvaluate { evaluatedProject ->
                    Errors.frontend.check(evaluatedProject.plugins.hasPlugin("io.github.diskria.projektor")) {
                        "Project '$path' was declared in settings.gradle.kts, " +
                            "but 'alias(convention.plugins.projektor)' plugin was not applied in its build.gradle.kts!"
                    }
                }
            }
        }
    }

    internal fun configureGradlePluginRepositories() {
        @Suppress("UnstableApiUsage")
        settings.dependencyResolutionManagement.repositories.apply {
            gradlePluginPortal()
            mavenCentrals()
        }
    }

    internal fun configureKotlinLibraryRepositories() {
        @Suppress("UnstableApiUsage")
        settings.dependencyResolutionManagement.repositories.apply {
            mavenCentrals()
        }
    }
}

private fun RepositoryHandler.mavenCentrals() {
    mavenCentral { it.name = "ApacheMavenCentral" }
    maven("https://repo1.maven.org/maven2") { name = "SonatypeMavenCentral" }
}
