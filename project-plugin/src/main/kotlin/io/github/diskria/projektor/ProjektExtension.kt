package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.configurations.*
import io.github.diskria.projektor.configurators.*
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.PublishingTarget
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : GradleExtension() {

    val publishingTarget: Property<PublishingTarget> = objects.property(PublishingTarget::class.java)

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

    fun buildProjekt(metadata: ProjektMetadata): Projekt =
        Projekt(
            owner = metadata.owner,
            developer = metadata.developer,
            email = "diskria@proton.me",
            repo = metadata.repo,
            name = metadata.name,
            description = metadata.description,
            version = metadata.version,
            tags = metadata.tags,
            license = metadata.license,
            publishingTarget = publishingTarget.orNull,
            javaVersion = Versions.JAVA,
            kotlinVersion = Versions.KOTLIN,
        )

    private fun setConfigurator(configurator: Configurator<*>) {
        if (this.configurator != null) {
            gradleError("Projekt already configured!")
        }
        this.configurator = configurator
        onConfiguratorReadyCallback?.invoke(configurator)
    }
}
