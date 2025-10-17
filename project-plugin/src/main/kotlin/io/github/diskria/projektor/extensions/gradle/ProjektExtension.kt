package io.github.diskria.projektor.extensions.gradle

import io.github.diskria.projektor.common.extensions.gradle.ConfigurationExtension
import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.AndroidApplicationConfigurator
import io.github.diskria.projektor.configurators.AndroidLibraryConfigurator
import io.github.diskria.projektor.configurators.Configurator
import io.github.diskria.projektor.configurators.GradlePluginConfigurator
import io.github.diskria.projektor.configurators.KotlinLibraryConfigurator
import io.github.diskria.projektor.configurators.MinecraftModConfigurator

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