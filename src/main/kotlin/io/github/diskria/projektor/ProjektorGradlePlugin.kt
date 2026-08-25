package io.github.diskria.projektor

import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.api.ProjektMetadataExtension
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitAttributesTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitIgnoreTask
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.features.release.ReleaseProjektTask
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
import org.gradle.kotlin.dsl.findByType
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
                         id("io.github.diskria.projektor") version "8.0.2"
                     }
                
                  2. Then in 'build.gradle.kts':
                     plugins {
                         alias(convention.plugins.projektor)
                     }
                """.trimIndent()
            )
        }
    }

    private fun applyToSettings(settings: Settings) {
        settings.gradle.isProjektorSettingsApplied = true
        settings.ensurePluginApplied("org.gradle.toolchains.foojay-resolver-convention")
        @Suppress("UnstableApiUsage")
        settings.dependencyResolutionManagement.repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
        val extension = settings.registerExtension<ProjektMetadataExtension>(settings, name = "projekt")
        settings.gradle.settingsEvaluated {
            val (ownerName, repoName) = if (settings.providers.isCI) {
                with(settings.providers) { requireEnv("GITHUB_OWNER") to requireEnv("GITHUB_REPO") }
            } else {
                with(settings.rootDir) { parentFile.name to name }
            }
            val metadata = extension.ensureConfigured(ownerName, repoName, ProjektAbout.of(settings.rootDir))
            settings.gradle.rootProject { rootProject ->
                rootProject.projektMetadata = metadata
            }
        }
        configureVersionCatalogs(settings)
        settings.gradle.rootProject { rootProject ->
            setupEnvironment(rootProject)
        }
    }

    private fun configureVersionCatalogs(settings: Settings) {
        settings.dependencyResolutionManagement.versionCatalogs.maybeCreate("convention").apply {
            plugin("projektor", "io.github.diskria.projektor").version("")
        }
        val gradleDir = settings.rootDir.resolve("gradle")
        val defaultCatalog = gradleDir.resolve("libs.versions.toml")
        if (!defaultCatalog.exists()) {
            defaultCatalog.parentFile.mkdirs()
            defaultCatalog.createNewFile()
            defaultCatalog.writeText(VersionCatalogsHelper.TEMPLATE)
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

    private fun applyToProject(project: Project) {
        Errors.frontend.require(project.gradle.isProjektorSettingsApplied) {
            """
            Projektor plugin was applied in 'build.gradle.kts', but is missing from 'settings.gradle.kts'!
            
            Please add it to 'settings.gradle.kts' with a version first:
              plugins {
                  id("io.github.diskria.projektor") version "8.0.2"
              }
            
            And in 'build.gradle.kts', apply it WITHOUT a version:
              plugins {
                  alias(convention.plugins.projektor) 
              }
            """.trimIndent()
        }
        project.ensurePluginApplied("org.jetbrains.kotlin.jvm")
        project.ensurePluginApplied("org.jetbrains.kotlin.plugin.serialization")
        val extension = project.registerExtension<ProjektExtension>()
        project.afterEvaluate {
            extension.ensureConfigured(project)
        }
        configureReleaseTask(project.rootProject)
    }

    private fun configureReleaseTask(rootProject: Project) {
        if (rootProject.isProjektorReleaseConfigured) {
            return
        }
        val secrets = SecretsHelper(rootProject.providers)
        val gitAttributes = rootProject.tasks.registerTask<GenerateGitAttributesTask>(secrets)
        val gitIgnore = rootProject.tasks.registerTask<GenerateGitIgnoreTask>(secrets) { mustRunAfter(gitAttributes) }
        val license = rootProject.tasks.registerTask<GenerateLicenseTask>(secrets) { mustRunAfter(gitIgnore) }
        val readme = rootProject.tasks.registerTask<GenerateReadmeTask>(secrets) { mustRunAfter(license) }
        val githubMetadata = rootProject.tasks.registerTask<UpdateGithubRepoMetadataTask>(secrets) {
            mustRunAfter(readme)
        }
        val release = rootProject.tasks.registerTask<ReleaseProjektTask> {
            dependsOn(gitAttributes, gitIgnore, license, readme, githubMetadata)
        }
        rootProject.allprojects { subproject ->
            subproject.afterEvaluate {
                val publishingTaskNames = subproject.projektPublishingTaskNames
                if (publishingTaskNames.isNotEmpty()) {
                    val publishingTasks = subproject.tasks.matching { it.name in publishingTaskNames }
                    publishingTasks.configureEach { it.mustRunAfter(readme) }
                    githubMetadata.configure { it.mustRunAfter(publishingTasks) }
                    release.configure { it.dependsOn(publishingTasks) }
                }
            }
        }
        rootProject.gradle.projectsEvaluated {
            val allProjekts = rootProject.allprojects.mapNotNull { subproject ->
                subproject.extensions.findByType<ProjektExtension>()?.configuredProjekt?.orNull
            }
            readme.configure { it.projekts.set(allProjekts) }
            githubMetadata.configure { it.primaryProjekt.set(allProjekts.first()) }
        }
        rootProject.isProjektorReleaseConfigured = true
    }
}
