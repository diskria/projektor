package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.files
import io.github.diskria.kotlin.utils.extensions.common.camelCase
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.kotlin.utils.words.PascalCase
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra

class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        val extension = settings.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            val projekt = configurator.configure(settings, extension.buildProjekt(settings))
            settings.gradle.rootProject {
                description = projekt.description
                version = projekt.version

                val projektOwner by projekt.owner.toAutoNamedProperty()
                val projektDeveloper by projekt.developer.toAutoNamedProperty()
                val projektRepo by projekt.repo.toAutoNamedProperty()
                val projektName by projekt.name.toAutoNamedProperty()
                val projektTags by projekt.tags.toAutoNamedProperty()
                val projektLicenseId by projekt.license.id.toAutoNamedProperty()
                listOf(
                    projektOwner,
                    projektDeveloper,
                    projektRepo,
                    projektName,
                    projektTags,
                    projektLicenseId,
                ).forEach {
                    extra[it.name] = it.value
                }
            }
            if (extension.versionCatalogPath.isPresent) {
                configureVersionCatalog(settings, extension.versionCatalogPath.get())
            }
        }
        settings.gradle.settingsEvaluated {
            extension.onSettingsEvaluated()
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

    private inline fun <reified T : Any> ExtensionAware.registerExtension(vararg arguments: Any): T =
        extensions.create<T>(T::class.className().removeSuffix("Extension").setCase(PascalCase, camelCase), *arguments)
}
