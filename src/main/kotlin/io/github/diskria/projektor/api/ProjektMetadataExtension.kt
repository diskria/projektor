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
import java.io.File
import javax.inject.Inject

open class ProjektMetadataExtension @Inject internal constructor(
    private val settings: Settings,
    objects: ObjectFactory
) : ProjektorScope {

    val version = objects.property<String>()
    val email = objects.property<String>().convention("diskria@proton.me")

    internal val projektTypes = mutableSetOf<ProjektType>()

    internal var license: LicenseType? = null
        private set

    internal var isMonorepo: Boolean = false
        private set

    internal var primaryProjectPath: String? = null

    fun gradlePlugin() {
        ensureSingleRepoMode()
        configureGradlePluginRepositories()
        registerProjekt(":", ProjektType.GRADLE_PLUGIN)
    }

    fun kotlinLibrary() {
        ensureSingleRepoMode()
        configureKotlinLibraryRepositories()
        registerProjekt(":", ProjektType.KOTLIN_LIBRARY)
    }

    fun license(configure: LicensingDsl.() -> Unit) {
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

    internal fun ensureConfigured(
        ownerName: String,
        repoName: String,
        rootDirectory: File,
        isBuildLogic: Boolean,
    ): ProjektMetadata {
        Errors.frontend.check(projektTypes.isNotEmpty()) {
            "Projekt type is not configured in settings.gradle.kts! " +
                "Call kotlinLibrary(), gradlePlugin() or monorepo { ... }"
        }
        val repo = GithubRepo(GithubOwner(ownerName, email.get()), repoName)
        val packageName = "${repo.owner.namespace}.${repo.name.lowercase().replace("-", "_")}"
        val about = if (isBuildLogic) null else ProjektAbout.from(rootDirectory)
        val displayName = repo.name.split("-").joinToString(" ") { (about?.fixCase(it) ?: it).capitalized() }
        return if (about != null) {
            ProjektMetadata.Regular(
                isMonorepo = isMonorepo,
                projektTypes = projektTypes,
                repo = repo,
                packageName = packageName,
                displayName = displayName,
                version = version.getOrElse("0.1.0"),
                license = license,
                about = about,
            )
        } else {
            Errors.frontend.check(license == null) { "Build logic shouldn't have a license" }
            Errors.frontend.check(!version.isPresent) { "Build logic shouldn't have a version" }
            ProjektMetadata.BuildLogic(
                isMonorepo = isMonorepo,
                projektTypes = projektTypes,
                repo = repo,
                packageName = packageName,
                displayName = displayName,
            )
        }
    }

    internal fun registerProjekt(projectPath: String, type: ProjektType) {
        if (primaryProjectPath == null) {
            primaryProjectPath = projectPath
        }
        projektTypes.add(type)
        settings.gradle.rootProject { rootProject ->
            rootProject.project(projectPath) { project ->
                project.afterEvaluate {
                    Errors.frontend.check(project.plugins.hasPlugin("io.github.diskria.projektor")) {
                        "Project '$projectPath' was declared in settings.gradle.kts, " +
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
}

private fun RepositoryHandler.mavenCentrals() {
    mavenCentral { it.name = "ApacheMavenCentral" }
    maven("https://repo1.maven.org/maven2") { name = "SonatypeMavenCentral" }
}
