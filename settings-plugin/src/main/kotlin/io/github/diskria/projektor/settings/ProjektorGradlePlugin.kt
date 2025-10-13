package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.files
import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.extra

class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        val extension = settings.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            val metadata = extension.buildMetadata(settings)
            configureRootProject(settings, metadata)
            configurator.configure(settings, metadata)

            if (extension.versionCatalogPath.isPresent) {
                configureVersionCatalog(settings, extension.versionCatalogPath.get())
            }
        }
        settings.gradle.settingsEvaluated {
            extension.checkNotConfigured()
        }
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")
    }

    private fun configureRootProject(settings: Settings, metadata: ProjektMetadata) = with(settings) {
        rootProject.name = metadata.name.modifyIf(metadata.owner.first().isUpperCase()) {
            it.appendPrefix(metadata.owner + Constants.Char.SPACE)
        }
        gradle.rootProject {
            description = metadata.description
            version = metadata.version

            val projektMetadata by metadata.autoNamedProperty()
            extra[projektMetadata.name] = projektMetadata.value
        }
    }

    private fun configureVersionCatalog(settings: Settings, path: String) = with(settings) {
        dependencyResolutionManagement {
            versionCatalogs {
                create("libs") {
                    from(files(rootDir.resolve(path)))
                }
            }
        }
    }
}
