package io.github.diskria.projektor.extensions.gradle

import io.github.diskria.projektor.common.extensions.gradle.AbstractProjektExtension
import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.*
import io.github.diskria.projektor.configurators.common.ProjectConfigurator

open class ProjektExtension : AbstractProjektExtension<ProjectConfigurator<*>>() {

    fun gradlePlugin(configuration: GradlePluginConfiguration.() -> Unit = {}) {
        setConfigurator(GradlePluginConfigurator(GradlePluginConfiguration().apply(configuration)))
    }

    fun kotlinLibrary(configuration: KotlinLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(configuration)))
    }

    fun androidLibrary(configuration: AndroidLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(configuration)))
    }

    fun androidApplication(configuration: AndroidApplicationConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(configuration)))
    }

    fun minecraftMod(configuration: MinecraftModConfiguration.() -> Unit = {}) {
        setConfigurator(MinecraftModConfigurator(MinecraftModConfiguration().apply(configuration)))
    }
}
