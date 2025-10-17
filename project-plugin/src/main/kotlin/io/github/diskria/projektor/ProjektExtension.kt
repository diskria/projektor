package io.github.diskria.projektor

import io.github.diskria.projektor.common.extensions.ConfigurationExtension
import io.github.diskria.projektor.configurations.*
import io.github.diskria.projektor.configurators.*

open class ProjektExtension : ConfigurationExtension<Configurator<*>>() {

    fun gradlePlugin(block: GradlePluginConfiguration.() -> Unit = {}) {
        configure(GradlePluginConfigurator(GradlePluginConfiguration().apply(block)))
    }

    fun kotlinLibrary(block: KotlinLibraryConfiguration.() -> Unit = {}) {
        configure(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(block)))
    }

    fun androidLibrary(block: AndroidLibraryConfiguration.() -> Unit = {}) {
        configure(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(block)))
    }

    fun androidApplication(block: AndroidApplicationConfiguration.() -> Unit = {}) {
        configure(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(block)))
    }

    fun minecraftMod(block: MinecraftModConfiguration.() -> Unit = {}) {
        configure(MinecraftModConfigurator(MinecraftModConfiguration().apply(block)))
    }
}
