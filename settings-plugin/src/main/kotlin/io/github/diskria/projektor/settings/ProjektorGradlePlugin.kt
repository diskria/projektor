package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.files
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.gradle.utils.helpers.VersionCatalogsHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable
import io.github.diskria.projektor.common.extensions.setMetadata
import io.github.diskria.projektor.common.projekt.OwnerType
import io.github.diskria.projektor.common.projekt.metadata.AboutMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.metadata.github.GithubOwner
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.settings.extensions.gradle.ProjektExtension
import io.github.diskria.projektor.settings.extensions.mappers.mapToModel
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")

        val extension = settings.registerExtension<ProjektExtension>()
        extension.onConfigurationReady { projektType ->
            val metadata = extension.buildMetadata(buildGithubRepository(settings), AboutMetadata.of(settings.rootDir))
            configureRootProject(settings, metadata)
            projektType.mapToModel().configure(settings, metadata)

            if (extension.versionCatalogPath.isPresent) {
                configureCatalog(settings, extension.versionCatalogPath.get())
            }
            extension.extraRepositories.get().forEach { type ->
                type.mapToModel().configureRepositories(settings)
            }
        }
        settings.gradle.settingsEvaluated {
            extension.ensureConfigured()
        }
    }

    private fun configureRootProject(settings: Settings, metadata: ProjektMetadata) = with(settings) {
        val owner = metadata.repository.owner
        rootProject.name = metadata.name.modifyIf(owner.type == OwnerType.BRAND) {
            owner.name + Constants.Char.SPACE + it
        }
        gradle.rootProject {
            description = metadata.description
            version = metadata.version

            setMetadata(metadata)
        }
    }

    private fun configureCatalog(settings: Settings, path: String) = with(settings) {
        dependencyResolutionManagement {
            versionCatalogs {
                create(VersionCatalogsHelper.DEFAULT_CATALOG_NAME) {
                    from(files(rootDir.resolve(path)))
                }
            }
        }
    }

    private fun buildGithubRepository(settings: Settings): GithubRepository = with(settings) {
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
        return GithubRepository(
            GithubOwner(ownerType, ownerName, "diskria@proton.me"),
            repositoryName
        )
    }
}
