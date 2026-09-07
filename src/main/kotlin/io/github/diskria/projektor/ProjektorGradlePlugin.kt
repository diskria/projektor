package io.github.diskria.projektor

import io.github.diskria.projektor.api.BuildLogicProjektExtension
import io.github.diskria.projektor.api.DistributableProjektExtension
import io.github.diskria.projektor.api.ProjektMetadataExtension
import io.github.diskria.projektor.core.model.DistributableProjektModel
import io.github.diskria.projektor.core.model.ProjektModule
import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.core.model.metadata.ProjektMetadataBuildService
import io.github.diskria.projektor.extensions.*
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
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
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
    }

    private fun applyToDistributableSettings(settings: Settings) {
        val rootDirectory = settings.layout.rootDirectory
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")
        val defaultCatalogFile = rootDirectory.file("gradle/libs.versions.toml")
        if (!defaultCatalogFile.asFile.exists()) {
            defaultCatalogFile.writeTextCreatingParent(VersionCatalogsHelper.TEMPLATE)
        }
        val buildLogicDirectory = rootDirectory.dir("build-logic")
        if (buildLogicDirectory.asFile.exists()) {
            settings.includeBuild("build-logic")
            settings.pluginManagement.includeBuild("build-logic")
        }
        val extension = settings.extensions.create<ProjektMetadataExtension>(settings, name = "projektor")
        settings.gradle.settingsEvaluated {
            val env = EnvProvider(settings.providers)
            val (ownerName, repoName) = if (env.isCI) {
                env.githubOwner to env.githubRepo
            } else {
                with(rootDirectory.asFile) { parentFile.name to name }
            }
            val projektMetadata = extension.ensureConfigured(ownerName, repoName)
            if (projektMetadata.isMonorepo) settings.ensureRootSourcesEmpty()
            if (extension.buildLogicModules.isNotEmpty()) {
                buildLogicDirectory.file(BUILD_LOGIC_MODULES_PATH)
                    .writeTextCreatingParent(Json.encodeToString(extension.buildLogicModules))
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
        settings.dependencyResolutionManagement.versionCatalogs.register("libs") { catalog ->
            catalog.from(rootDirectory.files(rootDirectory.asFile.parentFile.resolve("gradle/libs.versions.toml")))
        }
        val modulesConfigFile = rootDirectory.file(BUILD_LOGIC_MODULES_PATH).asFile
        check(modulesConfigFile.exists()) {
            """
            Build logic project cannot be built standalone!
            It depends on the host project because its configuration is defined there.
            Please build from the root project instead.
            """.trimIndent()
        }
        val projektMetadata = ProjektMetadata.BuildLogic(Json.decodeFromString(modulesConfigFile.readText()))
        if (projektMetadata.isMonorepo) settings.ensureRootSourcesEmpty()
        ProjektMetadataExtension.applyModules(projektMetadata, settings)
        settings.registerProjektMetadataBuildService(projektMetadata)
    }

    private fun Settings.ensureRootSourcesEmpty() {
        val srcDirectory = layout.rootDirectory.dir("src")
        check(!srcDirectory.asFile.exists()) {
            """
            Root project source directory '${srcDirectory.asFile.path}' is not allowed in a monorepo!
            Move your source code into a subprojects.
            """.trimIndent()
        }
    }

    private fun Settings.registerProjektMetadataBuildService(projektMetadata: ProjektMetadata) {
        gradle.sharedServices.registerIfAbsent<ProjektMetadataBuildService, ProjektMetadataBuildService.Parameters> {
            it.parameters.projektMetadata.set(projektMetadata)
        }
    }

    private fun configureBuildLogicVersionCatalog(settings: Settings, modules: List<ProjektModule>) {
        val plugins = modules.filter { it.type == ProjektType.GRADLE_PLUGIN }
        val libraries = modules.filter { it.type == ProjektType.KOTLIN_LIBRARY }
        if (plugins.isEmpty() && libraries.isEmpty()) return
        settings.dependencyResolutionManagement.versionCatalogs.register("builder") { catalog ->
            plugins.forEach { catalog.plugin(it.name, "builder.${it.name}").version("") }
            libraries.forEach { catalog.library(it.name, "builder", it.name).withoutVersion() }
        }
    }

    private fun setupEnvironment(rootProject: Project) {
        val required = GradleVersion.version("9.7.1")
        rootProject.tasks.withType<Wrapper>().configureEach { wrapper ->
            wrapper.gradleVersion = required.version
            wrapper.distributionType = Wrapper.DistributionType.ALL
        }
        val requestedTasks = rootProject.gradle.startParameter.taskNames
        if (requestedTasks.none() || requestedTasks.any { it.substringAfterLast(":") != "wrapper" }) {
            val current = GradleVersion.current()
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
        val serviceProvider = project.gradle.sharedServices.findByType<ProjektMetadataBuildService>()
        val projektMetadata = checkNotNull(serviceProvider?.orNull?.projektMetadata?.orNull) {
            """
            Projektor metadata is missing for project '${project.path}'.
            
            To fix this, ensure 'settings.gradle.kts' is configured:
            
            1. Apply the plugin: 
              plugins {
                  id("$ID") version "$VERSION"
              }
            
            2. Configure projektor modules:
              projektor {
                  // ...
              }
            """.trimIndent()
        }
        if (project == project.rootProject) {
            if (projektMetadata is ProjektMetadata.Distributable) configureReleaseTask(project, projektMetadata)
            if (projektMetadata.isMonorepo) return
        }
        project.applyKotlinPlugins()
        when (projektMetadata) {
            is ProjektMetadata.Distributable -> {
                val extension = project.extensions.create<DistributableProjektExtension>(name = "projekt")
                project.afterEvaluate {
                    extension.ensureConfigured(project, projektMetadata)
                }
            }

            is ProjektMetadata.BuildLogic -> {
                val extension = project.extensions.create<BuildLogicProjektExtension>(name = "projekt")
                project.afterEvaluate {
                    extension.ensureConfigured(project, projektMetadata)
                }
            }
        }
    }

    private fun Project.applyKotlinPlugins() {
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
    }

    private fun configureReleaseTask(rootProject: Project, projektMetadata: ProjektMetadata.Distributable) {
        if (rootProject.tasks.isRegistered<ReleaseProjektTask>()) return
        val distributableProjektModels = collectSubprojectConfig(
            DISTRIBUTABLE_PROJEKT_MODEL_CONFIGURATION_NAME, rootProject, projektMetadata.modules
        ).flatMap { config ->
            config.incoming.files.elements.map { locations ->
                locations.map { Json.decodeFromString<DistributableProjektModel>(it.asFile.readText()) }
            }
        }
        val distributeProjektTasks = collectSubprojectConfig(
            DISTRIBUTE_PROJEKT_TASK_CONFIGURATION_NAME, rootProject, projektMetadata.modules
        )
        val generateGitAttributesTask = rootProject.tasks.register<GenerateGitAttributesTask> { task ->
            task.repo.set(projektMetadata.repo)
        }
        val generateGitIgnoreTask = rootProject.tasks.register<GenerateGitIgnoreTask> { task ->
            task.repo.set(projektMetadata.repo)
            task.mustRunAfter(generateGitAttributesTask)
        }
        val generateLicenseTask = projektMetadata.licenseType?.let { licenseType ->
            rootProject.tasks.register<GenerateLicenseTask> { task ->
                task.licenseType.set(licenseType)
                task.developer.set(projektMetadata.repo.owner.developer)
                task.repo.set(projektMetadata.repo)
                task.mustRunAfter(generateGitIgnoreTask)
            }
        }
        rootProject.configurations.consumable(ROOT_LICENSE_TASK_CONFIGURATION_NAME) { config ->
            generateLicenseTask?.let { task -> config.outgoing.artifact(task) }
        }
        val generateReadmeTask = rootProject.tasks.register<GenerateReadmeTask> { task ->
            task.displayName.set(projektMetadata.displayName)
            task.about.set(projektMetadata.about)
            task.licenseType.set(projektMetadata.licenseType)
            task.distributionTargetShieldMarkdowns.set(
                distributableProjektModels.map { it.flatMap { model -> model.readmeShieldMarkdowns } }
            )
            task.repo.set(projektMetadata.repo)
            task.mustRunAfter(generateLicenseTask ?: generateGitIgnoreTask)
        }
        val generateReleaseWorkflowTask = rootProject.tasks.register<GenerateReleaseWorkflowTask> { task ->
            task.repo.set(projektMetadata.repo)
            task.mustRunAfter(generateReadmeTask)
        }
        rootProject.tasks.register<UpdateGithubRepoMetadataTask> { task ->
            task.projektTypes.set(projektMetadata.modules.map { it.type })
            task.about.set(projektMetadata.about)
            task.repo.set(projektMetadata.repo)
            task.homepageUrl.set(
                distributableProjektModels.map { it.firstNotNullOfOrNull { model -> model.homepageUrl } }
            )
            task.mustRunAfter(generateReleaseWorkflowTask)
        }
        rootProject.tasks.register<ReleaseProjektTask> { task ->
            task.dependsOn(ReleaseProjektTask.PREPARATION_TASK_NAMES.mapNotNull { taskName ->
                if (rootProject.tasks.names.contains(taskName)) rootProject.tasks.named(taskName)
                else null
            })
            task.dependsOn(distributeProjektTasks)
        }
    }

    private fun collectSubprojectConfig(
        consumableName: String,
        rootProject: Project,
        modules: List<ProjektModule>,
    ): NamedDomainObjectProvider<ResolvableConfiguration> {
        val resolvableName = consumableName + "Resolver"
        val dependencyScope = rootProject.configurations.dependencyScope("${resolvableName}Scope")
        modules.forEach { module ->
            val dependency = rootProject.dependencies.project(
                mapOf("path" to module.path, "configuration" to consumableName)
            )
            rootProject.dependencies.add(dependencyScope.name, dependency)
        }
        return rootProject.configurations.resolvable(resolvableName) { it.extendsFrom(dependencyScope) }
    }

    internal companion object {
        const val ID: String = BuildConfig.PLUGIN_ID
        const val VERSION: String = BuildConfig.PLUGIN_VERSION

        internal const val DISTRIBUTABLE_PROJEKT_MODEL_CONFIGURATION_NAME = "distributableProjektModel"
        internal const val DISTRIBUTE_PROJEKT_TASK_CONFIGURATION_NAME = "distributeProjektTask"
        internal const val ROOT_LICENSE_TASK_CONFIGURATION_NAME = "rootLicenseTask"

        private const val BUILD_LOGIC_MODULES_PATH = ".gradle/$ID/build-logic-modules.json"

        fun readResourceText(path: String): String =
            ProjektorGradlePlugin::class.java.getResourceAsStream("/$path")?.bufferedReader()?.use { it.readText() }
                ?: error("Resource not found in plugin package: $path")
    }
}
