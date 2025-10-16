package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.files
import io.github.diskria.gradle.utils.extensions.put
import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.gradle.utils.helpers.VersionCatalogsHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.projekt.OwnerType
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.settings.extensions.mappers.mapToModel
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.extra

class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")

        val extension = settings.registerExtension<ProjektExtension>()
        extension.onConfigured { type ->
            val metadata = extension.buildMetadata(settings, type)
            configureRootProject(settings, metadata)
            type.mapToModel().configure(settings, metadata)

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

            val projektMetadata by metadata.autoNamedProperty()
            extra.put(projektMetadata)
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
}
