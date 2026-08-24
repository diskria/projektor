package io.github.diskria.projektor

import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.api.ProjektMetadataExtension
import io.github.diskria.projektor.core.model.github.GithubOwner
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateProjektReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateProjektGitAttributesTask
import io.github.diskria.projektor.features.generation.tasks.GenerateProjektGitIgnoreTask
import io.github.diskria.projektor.features.generation.tasks.GenerateProjektLicenseTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.internal.gradle.VersionCatalogsHelper
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.SecretsHelper
import io.github.diskria.projektor.internal.utils.require
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.plugins.PluginAware
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.withType
import org.gradle.util.GradleVersion

@Suppress("unused")
class ProjektorGradlePlugin : Plugin<PluginAware> {

    override fun apply(target: PluginAware) {
        when (target) {
            is Settings -> applyToSettings(target)
            is Project -> applyToProject(target)
            else -> Errors.frontend.error(
                """
                Projektor plugin cannot be applied to '${target.javaClass.simpleName}'.
                
                This plugin must be applied in two steps:
                  1. In 'settings.gradle.kts':
                     plugins {
                         id("io.github.diskria.projektor") version "8.0.1"
                     }
                
                  2. Then in 'build.gradle.kts':
                     plugins {
                         alias(convention.plugins.projektor)
                     }
                """.trimIndent()
            )
        }
    }

    private fun applyToSettings(settings: Settings) = with(settings) {
        gradle.isProjektorSettingsApplied = true
        ensurePluginApplied("org.gradle.toolchains.foojay-resolver-convention")
        @Suppress("UnstableApiUsage")
        dependencyResolutionManagement.repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
        val extension = registerExtension<ProjektMetadataExtension>(settings)
        gradle.settingsEvaluated {
            extension.ensureConfigured()
            val metadata = extension.buildMetadata(buildGithubRepository(this), ProjektAbout.of(rootDir))
            gradle.rootProject {
                it.projektMetadata = metadata
            }
        }
        configureVersionCatalogs(this)
        gradle.rootProject { rootProject ->
            setupEnvironment(rootProject)
        }
    }

    private fun buildGithubRepository(settings: Settings): GithubRepo = with(settings) {
        val (owner, repo) = if (providers.isCI) {
            val githubOwner = providers.requireEnv("GITHUB_OWNER")
            val githubRepo = providers.requireEnv("GITHUB_REPO")
            githubOwner to githubRepo
        } else {
            val owner = rootDir.parentFile.name
            val repo = rootDir.name
            owner to repo
        }
        return GithubRepo(GithubOwner(owner, "diskria@proton.me"), repo)
    }

    private fun configureVersionCatalogs(settings: Settings) = with(settings) {
        dependencyResolutionManagement.versionCatalogs.maybeCreate("convention").apply {
            plugin("projektor", "io.github.diskria.projektor").version("")
        }
        val gradleDir = rootDir.resolve("gradle")
        val defaultCatalog = gradleDir.resolve("libs.versions.toml")
        if (!defaultCatalog.exists()) {
            defaultCatalog.parentFile.mkdirs()
            defaultCatalog.createNewFile()
            defaultCatalog.writeText(VersionCatalogsHelper.TEMPLATE)
        }
    }

    private fun applyToProject(project: Project) = with(project) {
        Errors.frontend.require(project.gradle.isProjektorSettingsApplied) {
            """
            Projektor plugin was applied in 'build.gradle.kts', but is missing from 'settings.gradle.kts'!
            
            Please add it to 'settings.gradle.kts' with a version first:
              plugins {
                  id("io.github.diskria.projektor") version "8.0.1"
              }
            
            And in 'build.gradle.kts', apply it WITHOUT a version:
              plugins {
                  alias(convention.plugins.projektor) 
              }
            """.trimIndent()
        }
        val extension = registerExtension<ProjektExtension>(project.projektMetadata)
        with(rootProject.tasks) {
            val secrets = SecretsHelper(project.providers)
            ensureTaskRegistered<GenerateProjektGitAttributesTask>(secrets)
            ensureTaskRegistered<GenerateProjektGitIgnoreTask>(secrets)
            ensureTaskRegistered<GenerateProjektLicenseTask>(secrets)
            ensureTaskRegistered<GenerateProjektReadmeTask>(secrets) {
                publishingTargets.set(extension.publishingTargets)
            }
            ensureTaskRegistered<UpdateGithubRepoMetadataTask>(secrets) {
                publishingTargets.set(extension.publishingTargets)
            }
        }
        ensurePluginApplied("org.jetbrains.kotlin.jvm")
        ensurePluginApplied("org.jetbrains.kotlin.plugin.serialization")
        afterEvaluate {
            extension.ensureConfigured(project)
        }
    }

    private fun setupEnvironment(rootProject: Project) {
        val current = GradleVersion.current()
        val required = GradleVersion.version("9.7.1")
        rootProject.tasks.withType<Wrapper> {
            gradleVersion = required.version
            distributionType = Wrapper.DistributionType.ALL
        }
        rootProject.gradle.taskGraph.whenReady { graph ->
            val isOnlyWrapper = graph.allTasks.all { it.name == "wrapper" }
            if (!isOnlyWrapper) {
                Errors.frontend.require(current == required) {
                    """
                    Gradle version mismatch detected!
                    Current version: ${current.version}
                    Target convention version: ${required.version}
                    
                    To align your environment with the project standard, run:
                      ./gradlew wrapper
                    """.trimIndent()
                }
            }
        }
    }
}
