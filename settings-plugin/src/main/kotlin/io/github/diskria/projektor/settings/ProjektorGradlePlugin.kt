package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.gradle.utils.helpers.VersionCatalogsHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.*
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.helpers.EmailType
import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable
import io.github.diskria.projektor.common.extensions.setProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektAbout
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.repo.github.GithubOwner
import io.github.diskria.projektor.common.repo.github.GithubRepo
import io.github.diskria.projektor.settings.extensions.develocity
import io.github.diskria.projektor.settings.extensions.gradle.ProjektExtension
import io.github.diskria.projektor.settings.helpers.BuildscriptPatches
import io.ktor.http.*
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

@Suppress("unused")
class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) = with(settings) {
        BuildscriptPatches.patchLoomGsonCompatibility(settings)

        ensurePluginApplied("org.gradle.toolchains.foojay-resolver-convention")

        develocity {
            buildScan {
                termsOfUseUrl.set(
                    buildUrl("gradle.com") {
                        path("help", "legal-terms-of-use")
                    }.toString()
                )
                termsOfUseAgree.set("yes")
                publishing.onlyIf { false }
                uploadInBackground.set(false)
            }
        }

        val extension = registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            configurator.configure(this)

            val metadata = extension.buildMetadata(buildGithubRepository(this), ProjektAbout.of(rootDirectory))
            configureRootProject(this, metadata)
        }
        gradle.settingsEvaluated {
            extension.ensureConfigured()
        }

        configureVersionCatalogs(this)
    }

    private fun configureRootProject(settings: Settings, metadata: ProjektMetadata) = with(settings) {
//        val owner = metadata.repo.owner
//        rootProject.name = metadata.name.modifyIf(owner.type == GithubOwnerType.BRAND) {
//            owner.name + Constants.Char.SPACE + it
//        }
        gradle.rootProject {
            description = metadata.description
            version = metadata.version

            saveDependencyResolutionRepositories(this)
            setProjektMetadata(metadata)
        }
    }

    private fun buildGithubRepository(settings: Settings): GithubRepo = with(settings) {
        val (owner, repo) = if (EnvironmentHelper.isCI()) {
            val githubOwner by autoNamed.environmentVariable(isRequired = true)
            val githubRepo by autoNamed.environmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            val owner = rootDirectory.parentFile.asDirectory().name
            val repo = rootDirectory.name
            owner to repo
        }
        val email = EmailType.PROTON.buildAddress(ProjektBuildConfig.PLUGIN_DEVELOPER)
        val githubOwner = GithubOwner(owner, email)
        return GithubRepo(githubOwner, repo)
    }

    private fun configureVersionCatalogs(settings: Settings) = with(settings) {
        val catalogsDirectory = findGradleProjectRoot().resolve("gradle/version-catalogs").ensureDirectoryExists()
        catalogsDirectory.resolve(VersionCatalogsHelper.buildCatalogFileName()).ensureFileExists {
            writeText(VersionCatalogsHelper.TEMPLATE)
        }
        val catalogFiles = catalogsDirectory.listFilesWithExtension(Constants.File.Extension.TOML)
        dependencyResolutionManagement {
            versionCatalogs {
                catalogFiles.forEach { catalogFile ->
                    create(catalogFile.nameWithoutExtensions) {
                        from(files(catalogFile))
                    }
                }
            }
        }
    }
}
