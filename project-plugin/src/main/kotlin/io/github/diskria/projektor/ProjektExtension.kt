package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.projektor.configurations.*
import io.github.diskria.projektor.configurators.*

open class ProjektExtension : GradleExtension() {

    private var configurator: Configurator<*>? = null
    private var onConfiguratorReadyCallback: ((Configurator<*>) -> Unit)? = null

    fun onConfigured(callback: (Configurator<*>) -> Unit) {
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

    fun ensureConfigured() {
        if (configurator == null) {
            gradleError("Projekt not configured!")
        }
    }

    private fun setConfigurator(configurator: Configurator<*>) {
        if (this.configurator != null) {
            gradleError("Projekt already configured!")
        }
        this.configurator = configurator
        onConfiguratorReadyCallback?.invoke(configurator)
    }
}
