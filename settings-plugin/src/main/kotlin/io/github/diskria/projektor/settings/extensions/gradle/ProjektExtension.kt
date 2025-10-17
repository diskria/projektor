package io.github.diskria.projektor.settings.extensions.gradle

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.common.extensions.gradle.AbstractProjektExtension
import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.projekt.metadata.AboutMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.settings.configurations.*
import io.github.diskria.projektor.settings.configurators.*
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class ProjektExtension @Inject constructor(
    objects: ObjectFactory
) : AbstractProjektExtension<SettingsConfigurator>() {

    val version: Property<String> = objects.property(String::class.java)
    val license: Property<LicenseType> = objects.property(LicenseType::class.java)
    val publish: Property<PublishingTargetType> = objects.property(PublishingTargetType::class.java)
    val versionCatalogPath: Property<String> = objects.property(String::class.java)

    private var projektType: ProjektType? = null

    fun buildMetadata(repository: GithubRepository, about: AboutMetadata): ProjektMetadata =
        ProjektMetadata(
            type = projektType ?: gradleError("Projekt type not initialized"),
            repository = repository,
            name = repository.name.setCase(`kebab-case`, `Title Case`),
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            publishingTarget = requireProperty(publish, ::publish.name),
            description = about.description,
            tags = about.tags,
        )

    fun gradlePlugin(configuration: GradlePluginConfiguration.() -> Unit) {
        setConfigurator(GradlePluginConfigurator(GradlePluginConfiguration().apply(configuration)))
        projektType = ProjektType.GRADLE_PLUGIN
    }

    fun kotlinLibrary(configuration: KotlinLibraryConfiguration.() -> Unit) {
        setConfigurator(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(configuration)))
        projektType = ProjektType.KOTLIN_LIBRARY
    }

    fun androidLibrary(configuration: AndroidLibraryConfiguration.() -> Unit) {
        setConfigurator(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(configuration)))
        projektType = ProjektType.ANDROID_LIBRARY
    }

    fun androidApplication(configuration: AndroidApplicationConfiguration.() -> Unit) {
        setConfigurator(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(configuration)))
        projektType = ProjektType.ANDROID_APPLICATION
    }

    fun minecraftMod(configuration: MinecraftModConfiguration.() -> Unit) {
        setConfigurator(MinecraftModConfigurator(MinecraftModConfiguration().apply(configuration)))
        projektType = ProjektType.MINECRAFT_MOD
    }
}
