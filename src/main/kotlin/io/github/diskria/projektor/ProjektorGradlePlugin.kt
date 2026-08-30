package io.github.diskria.projektor

import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.api.ProjektMetadataExtension
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.ProjektModule
import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitAttributesTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitIgnoreTask
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import io.github.diskria.projektor.features.generation.tasks.GenerateReleaseWorkflowTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.features.release.ReleaseProjektTask
import io.github.diskria.projektor.internal.gradle.VersionCatalogsHelper
import io.github.diskria.projektor.internal.utils.Envs
import kotlinx.serialization.json.Json
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
            else -> error(
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
        settings.dependencyResolutionManagement.repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
        if (settings.layout.rootDirectory.asFile.name != "build-logic") {
            applyToDistributableSettings(settings)
        } else {
            applyToBuildLogicSettings(settings)
        }
        settings.dependencyResolutionManagement.versionCatalogs.create("convention").apply {
            plugin("projektor", "io.github.diskria.projektor").version("")
        }
    }

    private fun applyToDistributableSettings(settings: Settings) {
        val rootDirectory = settings.layout.rootDirectory
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")
        val defaultCatalogFile = rootDirectory.file("gradle/libs.versions.toml").asFile
        if (!defaultCatalogFile.exists()) {
            defaultCatalogFile.parentFile.mkdirs()
            defaultCatalogFile.writeText(VersionCatalogsHelper.TEMPLATE)
        }
        val buildLogicDirectory = rootDirectory.dir("build-logic").asFile
        if (buildLogicDirectory.exists()) {
            settings.includeBuild("build-logic")
            settings.pluginManagement.includeBuild("build-logic")
        }
        val extension = settings.extensions.create<ProjektMetadataExtension>(settings, name = "projekt")
        settings.gradle.settingsEvaluated {
            val envs = Envs(settings.providers)
            val (ownerName, repoName) = if (envs.isCI) {
                envs.githubOwner to envs.githubRepo
            } else {
                with(rootDirectory.asFile) { parentFile.name to name }
            }
            val metadata = extension.ensureConfigured(ownerName, repoName)
            if (metadata.isMonorepo) {
                val srcDirectory = rootDirectory.dir("src").asFile
                check(!srcDirectory.exists()) {
                    """
                    Root project source directory '${srcDirectory.absolutePath}' is not allowed in a monorepo!
                    Move your source code into a subproject.
                    """.trimIndent()
                }
            }
            if (extension.buildLogicModules.isNotEmpty()) {
                configureBuildLogicVersionCatalog(settings, extension.buildLogicModules)
                System.setProperty("projektorBuildLogicModules", Json.encodeToString(extension.buildLogicModules))
            }
            settings.gradle.rootProject { rootProject ->
                rootProject.projektMetadata = metadata
            }
        }
        settings.gradle.rootProject { rootProject ->
            setupEnvironment(rootProject)
        }
    }

    private fun applyToBuildLogicSettings(settings: Settings) {
        val rootDirectory = settings.layout.rootDirectory
        settings.dependencyResolutionManagement.versionCatalogs.create("libs").from(
            rootDirectory.files(rootDirectory.asFile.parentFile.resolve("gradle/libs.versions.toml"))
        )
        val modules: List<ProjektModule>? = System.getProperty("projektorBuildLogicModules")?.let {
            Json.decodeFromString(it)
        }
        checkNotNull(modules) {
            "Build logic not configured in settings.gradle.kts"
        }
        ProjektMetadataExtension.applyModules(modules, settings)
        settings.gradle.rootProject { rootProject ->
            rootProject.pluginManager.apply("base")
            rootProject.tasks.matching { it.group?.lowercase() == "build" }.configureEach { task ->
                task.dependsOn(modules.map { module ->
                    rootProject.project(module.path).tasks.matching { it.name == task.name }
                })
            }
            rootProject.projektMetadata = ProjektMetadata.BuildLogic(modules)
        }
        configureBuildLogicVersionCatalog(settings, modules, skipPlugins = true)
    }

    private fun configureBuildLogicVersionCatalog(
        settings: Settings,
        modules: List<ProjektModule>,
        skipPlugins: Boolean = false,
    ) {
        val plugins = if (skipPlugins) emptyList() else modules.filter { it.type == ProjektType.GRADLE_PLUGIN }
        val libraries = modules.filter { it.type == ProjektType.KOTLIN_LIBRARY }
        if (plugins.isEmpty() && libraries.isEmpty()) return
        settings.dependencyResolutionManagement.versionCatalogs.create("builder").apply {
            plugins.forEach { plugin(it.name, "builder.${it.name}").version("") }
            libraries.forEach { library(it.name, "builder", it.name).withoutVersion() }
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
                check(current == required) {
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
        val projektMetadata = checkNotNull(project.rootProject.projektMetadata) {
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
        val extension = project.extensions.create<ProjektExtension>()
        project.afterEvaluate {
            extension.ensureConfigured(project, projektMetadata)
        }
        if (projektMetadata is ProjektMetadata.Distributable) {
            configureReleaseTask(project.rootProject, projektMetadata)
        }
    }

    private fun configureReleaseTask(rootProject: Project, projektMetadata: ProjektMetadata.Distributable) {
        if (rootProject.tasks.has<ReleaseProjektTask>()) return
        val envs = Envs(rootProject.providers)
        val generateGitAttributesTask = rootProject.tasks.register<GenerateGitAttributesTask>(envs) {
            repo.set(projektMetadata.repo)
        }
        val generateGitIgnoreTask = rootProject.tasks.register<GenerateGitIgnoreTask>(envs) {
            repo.set(projektMetadata.repo)
            mustRunAfter(generateGitAttributesTask)
        }
        val generateLicenseTask = projektMetadata.licenseType?.let { licenseType ->
            rootProject.tasks.register<GenerateLicenseTask>(envs) {
                this.licenseType.set(licenseType)
                developer.set(projektMetadata.repo.owner.developer)
                repo.set(projektMetadata.repo)
                mustRunAfter(generateGitIgnoreTask)
            }
        }
        val generateReadmeTask = rootProject.tasks.register<GenerateReadmeTask>(envs) {
            displayName.set(projektMetadata.displayName)
            about.set(projektMetadata.about)
            license.set(projektMetadata.licenseType)
            repo.set(projektMetadata.repo)
            mustRunAfter(generateLicenseTask ?: generateGitIgnoreTask)
        }
        val generateReleaseWorkflowTask = rootProject.tasks.register<GenerateReleaseWorkflowTask>(envs) {
            repo.set(projektMetadata.repo)
            mustRunAfter(generateReadmeTask)
        }
        val updateGithubRepoMetadataTask = rootProject.tasks.register<UpdateGithubRepoMetadataTask>(envs) {
            projektTypes.set(projektMetadata.modules.map { it.type })
            about.set(projektMetadata.about)
            repo.set(projektMetadata.repo)
            mustRunAfter(generateReleaseWorkflowTask)
        }
        rootProject.tasks.register<ReleaseProjektTask> {
            dependsOn(
                listOfNotNull(
                    generateGitAttributesTask,
                    generateGitIgnoreTask,
                    generateLicenseTask,
                    generateReadmeTask,
                    generateReleaseWorkflowTask,
                    updateGithubRepoMetadataTask,
                )
            )
        }
        rootProject.gradle.projectsEvaluated {
            val projekts = rootProject.allprojects
                .mapNotNull { it.extensions.find<ProjektExtension>()?.configuredProjekt?.orNull }
                .filterIsInstance<Projekt.Distributable>()
            generateReadmeTask.configure { task ->
                task.distributionTargetShieldMarkdowns.set(
                    projekts.flatMap { projekt ->
                        projekt.distributionTargetTypes.mapNotNull { targetType ->
                            targetType.mapToModel().getReadmeShield(projekt)?.markdown
                        }
                    }
                )
            }
            updateGithubRepoMetadataTask.configure { task ->
                val primaryProjekt = projekts.firstOrNull()
                val primaryDistributionTarget = primaryProjekt?.distributionTargetTypes?.firstOrNull()?.mapToModel()
                task.homepageUrl.set(primaryDistributionTarget?.getHomepage(primaryProjekt))
            }
        }
    }

    internal companion object {
        fun readResourceText(path: String): String =
            ProjektorGradlePlugin::class.java.getResourceAsStream("/$path")?.bufferedReader()?.use { it.readText() }
                ?: error("Resource not found in plugin package: $path")
    }
}
