package io.github.diskria.projektor.api

import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.core.model.ProjektModule
import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubOwner
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mavenCentralWithDirect
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class ProjektMetadataExtension @Inject internal constructor(
    private val settings: Settings,
    objects: ObjectFactory,
) : ProjektorScope {

    val version = objects.property<String>().convention("0.1.0")
    val email = objects.property<String>().convention("diskria@proton.me")

    internal val modules = mutableListOf<ProjektModule>()
    internal val buildLogicModules = mutableListOf<ProjektModule>()

    internal var license: LicenseType? = null
        private set

    internal var isMonorepo: Boolean = false
        private set

    fun gradlePlugin(name: String? = null) {
        ensureSingleRepoMode()
        registerModule(":", ProjektType.GRADLE_PLUGIN, name)
    }

    fun kotlinLibrary(name: String? = null) {
        ensureSingleRepoMode()
        registerModule(":", ProjektType.KOTLIN_LIBRARY, name)
    }

    fun license(configure: LicensingDsl.() -> Unit) {
        LicensingDsl { license = it }.configure()
    }

    fun monorepo(configure: MonorepoDsl.() -> Unit) {
        check(modules.isEmpty()) {
            "Cannot configure 'monorepo { ... }' when a single-repo project type has already been declared!"
        }
        isMonorepo = true
        MonorepoDsl(this).configure()
    }

    fun buildLogic(configure: BuildLogicDsl.() -> Unit) {
        BuildLogicDsl(this).configure()
    }

    private fun ensureSingleRepoMode() {
        check(!isMonorepo) {
            "Cannot declare single-repo project types outside of existing 'monorepo { ... }' block!"
        }
        check(modules.isEmpty()) {
            "Single-repo supports only one project type! Use 'monorepo { ... }' for multiple modules."
        }
    }

    internal fun registerModule(path: String, type: ProjektType, name: String?, isBuildLogic: Boolean = false) {
        val module = ProjektModule(path, type, name ?: settings.layout.rootDirectory.asFile.name)
        if (isBuildLogic) {
            buildLogicModules += module
        } else {
            modules += module
        }
    }

    internal fun ensureConfigured(ownerName: String, repoName: String): ProjektMetadata.Distributable {
        check(modules.isNotEmpty()) {
            "Projekt type is not configured in settings.gradle.kts! " +
                "Call kotlinLibrary(), gradlePlugin() or monorepo { ... }"
        }
        val projektMetadata = ProjektMetadata.Distributable(
            isMonorepo = isMonorepo,
            modules = modules,
            repo = GithubRepo(GithubOwner(ownerName, email.get()), repoName),
            version = version.get(),
            licenseType = license,
            about = ProjektAbout.from(settings.layout.rootDirectory.asFile),
        )
        applyModules(projektMetadata, settings)
        return projektMetadata
    }

    internal companion object {
        fun applyModules(projektMetadata: ProjektMetadata, settings: Settings) {
            projektMetadata.modules.forEach { module ->
                settings.dependencyResolutionManagement.repositories.apply {
                    when (module.type) {
                        ProjektType.GRADLE_PLUGIN -> {
                            gradlePluginPortal { it.name = "GradlePluginPortal" }
                            mavenCentralWithDirect()
                        }

                        ProjektType.KOTLIN_LIBRARY -> {
                            mavenCentralWithDirect()
                        }
                    }
                }
                if (!module.isRoot) {
                    settings.include(module.path)
                }
            }
            settings.gradle.lifecycle.afterProject { project ->
                val module = projektMetadata.findModule(project) ?: return@afterProject
                check(project.plugins.hasPlugin(ProjektorGradlePlugin.ID)) {
                    "Project '${module.path}' was declared in settings.gradle.kts, " +
                        "but 'alias(convention.plugins.projektor)' plugin " +
                        "was not applied in its build.gradle.kts!"
                }
            }
        }
    }
}
