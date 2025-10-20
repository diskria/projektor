package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.files
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.gradle.utils.helpers.VersionCatalogsHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.buildEmail
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable
import io.github.diskria.projektor.common.extensions.setMetadata
import io.github.diskria.projektor.common.github.GithubOwner
import io.github.diskria.projektor.common.github.GithubRepo
import io.github.diskria.projektor.common.projekt.OwnerType
import io.github.diskria.projektor.common.projekt.metadata.AboutMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.settings.extensions.findRootDirectoryFromCompositeBuildOrNull
import io.github.diskria.projektor.settings.extensions.gradle.ProjektExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")

        val extension = settings.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady {
            it.configure(settings)

            val metadata = extension.buildMetadata(buildGithubRepository(settings), AboutMetadata.of(settings.rootDir))
            configureRootProject(settings, metadata)
        }
        settings.gradle.settingsEvaluated {
            extension.ensureConfigured()
        }

        configureVersionCatalogs(settings)
    }

    private fun configureRootProject(settings: Settings, metadata: ProjektMetadata) = with(settings) {
        val owner = metadata.repo.owner
        rootProject.name = metadata.name.modifyIf(owner.type == OwnerType.BRAND) {
            owner.name + Constants.Char.SPACE + it
        }
        gradle.rootProject {
            description = metadata.description
            version = metadata.version

            setMetadata(metadata)
        }
    }

    private fun buildGithubRepository(settings: Settings): GithubRepo = with(settings) {
        val (ownerName, repositoryName) = if (providers.isCI) {
            val githubOwner by autoNamed.environmentVariable(isRequired = true)
            val githubRepo by autoNamed.environmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            val ownerName = rootDir.parentFile.asDirectory().name
            val repositoryName = rootDir.name
            ownerName to repositoryName
        }
        val ownerType = when {
            ownerName.first().isUpperCase() -> OwnerType.BRAND
            ownerName.contains(Constants.Char.HYPHEN) -> OwnerType.DOMAIN
            else -> OwnerType.PROFILE
        }
        return GithubRepo(
            GithubOwner(ownerName, buildEmail("diskria", "proton.me")),
            repositoryName
        )
    }

    private fun configureVersionCatalogs(settings: Settings) = with(settings) {
        val rootDirectory = gradle.findRootDirectoryFromCompositeBuildOrNull() ?: rootDir
        val catalogsDirectory = rootDirectory.resolve("gradle/version-catalogs").ensureDirectoryExists()
        catalogsDirectory
            .resolve(VersionCatalogsHelper.buildCatalogFileName(VersionCatalogsHelper.DEFAULT_CATALOG_NAME))
            .ensureFileExists()
        val catalogFiles = catalogsDirectory.listFiles {
            it.isFile && !it.isHidden && it.extension == Constants.File.Extension.TOML
        }
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
