package io.github.diskria.projektor.settings.extensions.gradle

import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.common.extensions.gradle.IProjektExtension
import io.github.diskria.projektor.common.licenses.LicenseType
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
) : IProjektExtension<
        SettingsConfigurator,
        GradlePluginConfiguration,
        KotlinLibraryConfiguration,
        AndroidLibraryConfiguration,
        AndroidApplicationConfiguration,
        MinecraftModConfiguration,
        >() {

    val version: Property<String> = objects.property(String::class.java)
    val license: Property<LicenseType> = objects.property(LicenseType::class.java)
    val publish: Property<PublishingTargetType> = objects.property(PublishingTargetType::class.java)

    val versionCatalogPath: Property<String> = objects.property(String::class.java)

    fun buildMetadata(repository: GithubRepository, about: AboutMetadata): ProjektMetadata =
        ProjektMetadata(
            type = getProjektType(),
            repository = repository,
            name = repository.name.setCase(`kebab-case`, `Title Case`),
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            publishingTarget = requireProperty(publish, ::publish.name),
            description = about.description,
            tags = about.tags,
        )

    override fun configureGradlePlugin(configuration: GradlePluginConfiguration.() -> Unit) {
        setConfigurator(GradlePluginConfigurator(GradlePluginConfiguration().apply(configuration)))
    }

    override fun configureKotlinLibrary(configuration: KotlinLibraryConfiguration.() -> Unit) {
        setConfigurator(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(configuration)))
    }

    override fun configureAndroidLibrary(configuration: AndroidLibraryConfiguration.() -> Unit) {
        setConfigurator(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(configuration)))
    }

    override fun configureAndroidApplication(configuration: AndroidApplicationConfiguration.() -> Unit) {
        setConfigurator(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(configuration)))
    }

    override fun configureMinecraftMod(configuration: MinecraftModConfiguration.() -> Unit) {
        setConfigurator(MinecraftModConfigurator(MinecraftModConfiguration().apply(configuration)))
    }
}
