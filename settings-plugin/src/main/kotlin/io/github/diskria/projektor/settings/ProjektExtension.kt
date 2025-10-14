package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.properties.AutoNamedEnvironmentVariable
import io.github.diskria.projektor.common.licenses.License
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.settings.configurators.*
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : GradleExtension() {

    val description: Property<String> = objects.property(String::class.java)
    val tags: SetProperty<String> = objects.setProperty(String::class.java)
    val version: Property<String> = objects.property(String::class.java)
    val license: Property<License> = objects.property(License::class.java)
    val versionCatalogPath: Property<String> = objects.property(String::class.java)

    private var configurator: Configurator? = null
    private var onConfiguratorReadyCallback: ((Configurator) -> Unit)? = null

    fun onConfiguratorReady(callback: (Configurator) -> Unit) {
        onConfiguratorReadyCallback = callback
    }

    fun gradlePlugin() {
        setConfigurator(GradlePluginConfigurator())
    }

    fun kotlinLibrary() {
        setConfigurator(KotlinLibraryConfigurator())
    }

    fun androidLibrary() {
        setConfigurator(AndroidLibraryConfigurator())
    }

    fun androidApplication() {
        setConfigurator(AndroidApplicationConfigurator())
    }

    fun minecraftMod() {
        setConfigurator(MinecraftModConfigurator())
    }

    fun buildMetadata(settings: Settings): ProjektMetadata = with(settings) {
        val (owner, repo) = if (providers.isCI) {
            val githubOwner by AutoNamedEnvironmentVariable(isRequired = true)
            val githubRepo by AutoNamedEnvironmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            val localOwner = rootDir.parentFile.asDirectory().name
            val localRepo = rootDir.name
            localOwner to localRepo
        }
        ProjektMetadata(
            owner = owner,
            developer = owner.substringBefore(Constants.Char.HYPHEN),
            email = "diskria@proton.me",
            repo = repo,
            name = repo.setCase(`kebab-case`, `Title Case`),
            description = requireProperty(description, ::description.name),
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            tags = requireProperty(tags, ::tags.name).ifEmpty { gradleError("Projekt tags must not be empty!") },
        )
    }

    fun checkNotConfigured() {
        if (configurator == null) {
            gradleError("Projekt not configured!")
        }
    }

    private fun setConfigurator(configurator: Configurator) {
        if (this.configurator != null) {
            gradleError("Projekt already configured!")
        }
        this.configurator = configurator
        onConfiguratorReadyCallback?.invoke(configurator)
    }
}
