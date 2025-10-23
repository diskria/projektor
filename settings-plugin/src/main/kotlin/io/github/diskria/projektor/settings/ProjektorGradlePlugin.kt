package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.files
import io.github.diskria.gradle.utils.extensions.findProjectRoot
import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.gradle.utils.helpers.VersionCatalogsHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.buildEmail
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.listFilesWithExtension
import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable
import io.github.diskria.projektor.common.extensions.setProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektAbout
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.repo.github.GithubOwner
import io.github.diskria.projektor.common.repo.github.GithubOwnerType
import io.github.diskria.projektor.common.repo.github.GithubRepo
import io.github.diskria.projektor.settings.extensions.gradle.ProjektExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")

        val extension = settings.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady {
            it.configure(settings)

            val metadata = extension.buildMetadata(buildGithubRepository(settings), ProjektAbout.of(settings.rootDir))
            configureRootProject(settings, metadata)
        }
        settings.gradle.settingsEvaluated {
            extension.ensureConfigured()
        }

        configureVersionCatalogs(settings)
    }

    private fun configureRootProject(settings: Settings, metadata: ProjektMetadata) = with(settings) {
        val owner = metadata.repo.owner
        rootProject.name = metadata.name.modifyIf(owner.type == GithubOwnerType.BRAND) {
            owner.name + Constants.Char.SPACE + it
        }
        gradle.rootProject {
            description = metadata.description
            version = metadata.version

            setProjektMetadata(metadata)
        }
    }

    private fun buildGithubRepository(settings: Settings): GithubRepo = with(settings) {
        val (owner, repo) = if (EnvironmentHelper.isCI()) {
            val githubOwner by autoNamed.environmentVariable(isRequired = true)
            val githubRepo by autoNamed.environmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            val owner = rootDir.parentFile.asDirectory().name
            val repo = rootDir.name
            owner to repo
        }
        return GithubRepo(GithubOwner(owner, buildEmail(ProjektBuildConfig.PLUGIN_DEVELOPER, "proton.me")), repo)
    }

    private fun configureVersionCatalogs(settings: Settings) = with(settings) {
        val catalogsDirectory = findProjectRoot().resolve("gradle/version-catalogs").ensureDirectoryExists()
        catalogsDirectory.resolve(VersionCatalogsHelper.buildCatalogFileName()).ensureFileExists()
        val catalogFiles = catalogsDirectory.listFilesWithExtension(Constants.File.Extension.TOML)
        dependencyResolutionManagement {
            versionCatalogs {
                catalogFiles.forEach { catalogFile ->
                    create(catalogFile.name.substringBefore(Constants.Char.DOT)) {
                        from(files(catalogFile))
                    }
                }
            }
        }
    }
}
