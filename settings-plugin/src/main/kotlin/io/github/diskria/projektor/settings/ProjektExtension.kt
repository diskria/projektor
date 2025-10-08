package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.properties.AutoNamedEnvironmentVariable
import io.github.diskria.projektor.settings.configurations.*
import io.github.diskria.projektor.settings.configurators.*
import io.github.diskria.projektor.settings.licenses.License
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import io.github.diskria.projektor.settings.projekt.common.Projekt
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : GradleExtension<Settings>() {

    val description: Property<String> = objects.property(String::class.java)
    val tags: SetProperty<String> = objects.setProperty(String::class.java)
    val version: Property<String> = objects.property(String::class.java)
    val license: Property<License> = objects.property(License::class.java)
    val versionCatalogPath: Property<String> = objects.property(String::class.java)

    private var configurator: Configurator<*>? = null
    private var onConfiguratorReadyCallback: ((Configurator<*>) -> Unit)? = null

    fun onConfiguratorReady(callback: (Configurator<*>) -> Unit) {
        onConfiguratorReadyCallback = callback
    }

    fun gradlePlugin(block: GradlePluginConfiguration.() -> Unit = {}) {
        setConfigurator(GradlePluginConfigurator(GradlePluginConfiguration().apply(block)))
    }

    fun kotlinLibrary(block: KotlinLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(block)))
    }

    fun androidLibrary(block: AndroidLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(block)))
    }

    fun androidApplication(block: AndroidApplicationConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(block)))
    }

    fun minecraftMod(block: MinecraftModConfiguration.() -> Unit = {}) {
        setConfigurator(MinecraftModConfigurator(MinecraftModConfiguration().apply(block)))
    }

    fun buildProjekt(settings: Settings): IProjekt = with(settings) {
        val (owner, repo) = if (providers.isCI) {
            val githubOwner by AutoNamedEnvironmentVariable(isRequired = true)
            val githubRepo by AutoNamedEnvironmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            val localOwner = rootDir.parentFile.asDirectory().name
            val localRepo = rootDir.name
            localOwner to localRepo
        }
        Projekt(
            owner = owner,
            repo = repo,
            description = requireProperty(description, ::description.name),
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            tags = requireProperty(tags, ::tags.name),
        )
    }

    fun onSettingsEvaluated() {
        if (configurator == null) {
            gradleError("Projekt not configured!")
        }
        configurator = null
        onConfiguratorReadyCallback = null
    }

    private fun setConfigurator(configurator: Configurator<*>) {
        if (this.configurator != null) {
            gradleError("Projekt already configured!")
        }
        this.configurator = configurator
        onConfiguratorReadyCallback?.invoke(configurator)
    }
}
