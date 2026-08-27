package io.github.diskria.projektor

import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.api.ProjektMetadataExtension
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitAttributesTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitIgnoreTask
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.features.release.ReleaseProjektTask
import io.github.diskria.projektor.internal.gradle.VersionCatalogsHelper
import io.github.diskria.projektor.internal.utils.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.plugins.PluginAware
import org.gradle.api.tasks.TaskProvider
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
                         id("io.github.diskria.projektor") version "8.0.6"
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
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")
        settings.dependencyResolutionManagement.repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
        val extension = settings.extensions.registerExtension<ProjektMetadataExtension>(settings, name = "projekt")
        val rootDirectory = settings.layout.rootDirectory
        val buildLogicDirectoryName = "build-logic"
        val isBuildLogic = rootDirectory.asFile.name == buildLogicDirectoryName
        settings.gradle.settingsEvaluated {
            val (ownerName, repoName) = if (settings.providers.isCI) {
                with(settings.providers) { requireEnv("GITHUB_OWNER") to requireEnv("GITHUB_REPO") }
            } else {
                with(rootDirectory.asFile) { parentFile.name to name }
            }
            val metadata = extension.ensureConfigured(ownerName, repoName, rootDirectory.asFile, isBuildLogic)
            settings.gradle.rootProject {
                if (metadata.isMonorepo) {
                    val srcDirectory = rootDirectory.dir("src").asFile
                    Errors.frontend.check(!srcDirectory.exists()) {
                        """
                        Root project source directory '${srcDirectory.absolutePath}' is not allowed in a monorepo!
                        Move your source code into a subproject.
                        """.trimIndent()
                    }
                }
                it.projektMetadata = metadata
            }
        }
        val defaultCatalogPath = "gradle/libs.versions.toml"
        if (isBuildLogic) {
            settings.dependencyResolutionManagement.versionCatalogs.create("libs").from(
                rootDirectory.files(rootDirectory.asFile.parentFile.resolve(defaultCatalogPath))
            )
        } else {
            val defaultCatalog = rootDirectory.file(defaultCatalogPath).asFile
            if (!defaultCatalog.exists()) {
                defaultCatalog.parentFile.mkdirs()
                defaultCatalog.writeText(VersionCatalogsHelper.TEMPLATE)
            }
            val buildLogicDirectory = rootDirectory.dir(buildLogicDirectoryName).asFile
            if (buildLogicDirectory.exists()) {
                settings.includeBuild(buildLogicDirectoryName)
                settings.pluginManagement.includeBuild(buildLogicDirectoryName)
            }
        }
        settings.dependencyResolutionManagement.versionCatalogs.create("convention").apply {
            plugin("projektor", "io.github.diskria.projektor").version("")
        }
        settings.gradle.rootProject { rootProject ->
            setupEnvironment(rootProject)
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
            val isWrapperOnly = graph.allTasks.all { it.name == "wrapper" }
            if (!isWrapperOnly) {
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
        val projektMetadata = project.rootProject.findProjektMetadata()
        Errors.frontend.requireNotNull(projektMetadata) {
            """
            Projektor plugin was applied in 'build.gradle.kts', but is missing from 'settings.gradle.kts'!
            
            Please add it to 'settings.gradle.kts' with a version first:
              plugins {
                  id("io.github.diskria.projektor") version "8.0.6"
              }
            
            And in 'build.gradle.kts', apply it WITHOUT a version:
              plugins {
                  alias(convention.plugins.projektor) 
              }
            """.trimIndent()
        }
        project.pluginManager.apply("org.jetbrains.kotlin.jvm")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        val extension = project.extensions.registerExtension<ProjektExtension>()
        project.afterEvaluate {
            extension.ensureConfigured(project)
        }
        if (projektMetadata is ProjektMetadata.Regular) {
            configureReleaseTask(project.rootProject, projektMetadata)
        }
    }

    private fun configureReleaseTask(rootProject: Project, projektMetadata: ProjektMetadata.Regular) {
        if (rootProject.tasks.hasTask<ReleaseProjektTask>()) return
        val secrets = SecretsHelper(rootProject.providers)
        val generateGitAttributes = rootProject.tasks.registerTask<GenerateGitAttributesTask>(secrets)
        val generateGitIgnore = rootProject.tasks.registerTask<GenerateGitIgnoreTask>(secrets) {
            mustRunAfter(generateGitAttributes)
        }
        var previousTask: TaskProvider<*> = generateGitIgnore
        val generateLicense = projektMetadata.license?.let { license ->
            rootProject.tasks.registerTask<GenerateLicenseTask>(secrets) {
                this.license.set(license)
                mustRunAfter(generateGitIgnore)
            }.also { previousTask = it }
        }
        val generateReadme = rootProject.tasks.registerTask<GenerateReadmeTask>(secrets) {
            about.set(projektMetadata.about)
            license.set(projektMetadata.license)
            mustRunAfter(previousTask)
        }
        val updateGithubRepoMetadata = rootProject.tasks.registerTask<UpdateGithubRepoMetadataTask>(secrets) {
            projektTypes.set(projektMetadata.projektTypes)
            about.set(projektMetadata.about)
            repo.set(projektMetadata.repo)
            mustRunAfter(generateReadme)
        }
        val releaseProjekt = rootProject.tasks.registerTask<ReleaseProjektTask> {
            dependsOn(
                listOfNotNull(
                    generateGitAttributes,
                    generateGitIgnore,
                    generateLicense,
                    generateReadme,
                    updateGithubRepoMetadata,
                )
            )
        }
        rootProject.allprojects { subproject ->
            subproject.afterEvaluate {
                subproject.projektDistributeTaskNames.forEach { taskName ->
                    val distributeTask = subproject.tasks.named(taskName) {
                        it.mustRunAfter(generateReadme)
                    }
                    updateGithubRepoMetadata.configure { it.mustRunAfter(distributeTask) }
                    releaseProjekt.configure { it.dependsOn(distributeTask) }
                }
            }
        }
        rootProject.gradle.projectsEvaluated {
            val projekts = rootProject.allprojects
                .mapNotNull { it.extensions.findByType<ProjektExtension>()?.configuredProjekt?.orNull }
                .filterIsInstance<Projekt.Regular>()
            val primaryProjekt = projekts.first()
            val distributionTargetTypes = projekts.flatMap { it.distributionTargetTypes }
            generateReadme.configure {
                it.projekts.set(projekts)
                it.distributionTargetTypes.set(distributionTargetTypes)
            }
            updateGithubRepoMetadata.configure {
                val primaryDistributionTarget = primaryProjekt.distributionTargetTypes.firstOrNull()?.mapToModel()
                it.homepageUrl.set(primaryDistributionTarget?.getHomepage(primaryProjekt))
            }
        }
    }
}
