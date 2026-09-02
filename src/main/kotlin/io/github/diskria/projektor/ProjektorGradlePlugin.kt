package io.github.diskria.projektor

import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.api.ProjektMetadataExtension
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.ProjektModule
import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.core.model.metadata.ProjektMetadataBuildService
import io.github.diskria.projektor.extensions.create
import io.github.diskria.projektor.extensions.find
import io.github.diskria.projektor.extensions.has
import io.github.diskria.projektor.extensions.register
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitAttributesTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitIgnoreTask
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import io.github.diskria.projektor.features.generation.tasks.GenerateReleaseWorkflowTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.features.release.ReleaseProjektTask
import io.github.diskria.projektor.generated.BuildConfig
import io.github.diskria.projektor.generated.EnvProvider
import io.github.diskria.projektor.internal.gradle.VersionCatalogsHelper
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
                         id("$ID") version "$VERSION"
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
            plugin("projektor", ID).version("")
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
            val env = EnvProvider(settings.providers)
            val (ownerName, repoName) = if (env.isCI) {
                env.githubOwner to env.githubRepo
            } else {
                with(rootDirectory.asFile) { parentFile.name to name }
            }
            val projektMetadata = extension.ensureConfigured(ownerName, repoName)
            if (projektMetadata.isMonorepo) {
                val srcDirectory = rootDirectory.dir("src").asFile
                check(!srcDirectory.exists()) {
                    """
                    Root project source directory '${srcDirectory.absolutePath}' is not allowed in a monorepo!
                    Move your source code into a subproject.
                    """.trimIndent()
                }
            }
            if (extension.buildLogicModules.isNotEmpty()) {
                val modulesConfigFile = buildLogicDirectory.resolve(MODULES_CONFIG_PATH)
                modulesConfigFile.parentFile.mkdirs()
                modulesConfigFile.writeText(Json.encodeToString(extension.buildLogicModules))
                configureBuildLogicVersionCatalog(settings, extension.buildLogicModules)
            }
            settings.registerProjektMetadataBuildService(projektMetadata)
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
        val modulesConfigFile = rootDirectory.file(MODULES_CONFIG_PATH).asFile
        check(modulesConfigFile.exists()) {
            """
            Build logic project cannot be built standalone!
            It depends on the host project because its configuration is defined there.
            Please build from the root project instead.
            """.trimIndent()
        }
        val projektMetadata = ProjektMetadata.BuildLogic(Json.decodeFromString(modulesConfigFile.readText()))
        ProjektMetadataExtension.applyModules(projektMetadata.modules, settings)
        settings.gradle.rootProject { rootProject ->
            settings.registerProjektMetadataBuildService(projektMetadata)
        }
    }

    private fun Settings.registerProjektMetadataBuildService(projektMetadata: ProjektMetadata) {
        gradle.sharedServices.register<ProjektMetadataBuildService, ProjektMetadataBuildService.Parameters> {
            parameters.projektMetadata.set(projektMetadata)
        }
    }

    private fun configureBuildLogicVersionCatalog(settings: Settings, modules: List<ProjektModule>) {
        val plugins = modules.filter { it.type == ProjektType.GRADLE_PLUGIN }
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
        val requestedTasks = rootProject.gradle.startParameter.taskNames
        if (requestedTasks.none() || requestedTasks.any { it.substringAfterLast(":") != "wrapper" }) {
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

    private fun applyToProject(project: Project) {
        val projektMetadata = checkNotNull(
            project.gradle.sharedServices.find<ProjektMetadataBuildService>()?.projektMetadata?.orNull
        ) {
            """
            Projektor plugin was applied in 'build.gradle.kts', but is missing from 'settings.gradle.kts'!
            
            Please add it to 'settings.gradle.kts' with a version first:
              plugins {
                  id("$ID") version "$VERSION"
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
        val env = EnvProvider(rootProject.providers)
        val generateGitAttributesTask = rootProject.tasks.register<GenerateGitAttributesTask>(env) {
            repo.set(projektMetadata.repo)
        }
        val generateGitIgnoreTask = rootProject.tasks.register<GenerateGitIgnoreTask>(env) {
            repo.set(projektMetadata.repo)
            mustRunAfter(generateGitAttributesTask)
        }
        val generateLicenseTask = projektMetadata.licenseType?.let { licenseType ->
            rootProject.tasks.register<GenerateLicenseTask>(env) {
                this.licenseType.set(licenseType)
                developer.set(projektMetadata.repo.owner.developer)
                repo.set(projektMetadata.repo)
                mustRunAfter(generateGitIgnoreTask)
            }
        }
        val generateReadmeTask = rootProject.tasks.register<GenerateReadmeTask>(env) {
            displayName.set(projektMetadata.displayName)
            about.set(projektMetadata.about)
            license.set(projektMetadata.licenseType)
            repo.set(projektMetadata.repo)
            mustRunAfter(generateLicenseTask ?: generateGitIgnoreTask)
        }
        val generateReleaseWorkflowTask = rootProject.tasks.register<GenerateReleaseWorkflowTask>(env) {
            repo.set(projektMetadata.repo)
            mustRunAfter(generateReadmeTask)
        }
        val updateGithubRepoMetadataTask = rootProject.tasks.register<UpdateGithubRepoMetadataTask>(env) {
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
        const val ID: String = BuildConfig.PLUGIN_ID
        const val VERSION: String = BuildConfig.PLUGIN_VERSION

        private const val MODULES_CONFIG_PATH = ".gradle/projektor/modules.json"

        fun readResourceText(path: String): String =
            ProjektorGradlePlugin::class.java.getResourceAsStream("/$path")?.bufferedReader()?.use { it.readText() }
                ?: error("Resource not found in plugin package: $path")
    }
}
