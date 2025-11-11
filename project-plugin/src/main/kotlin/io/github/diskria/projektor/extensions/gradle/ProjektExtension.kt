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

    fun gradlePlugin(configure: GradlePluginConfiguration.() -> Unit = {}) {
        setConfigurator(GradlePluginConfigurator(GradlePluginConfiguration().apply(configure)))
    }

    fun kotlinLibrary(configure: KotlinLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(configure)))
    }

    fun androidLibrary(configure: AndroidLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(configure)))
    }

    fun androidApplication(configure: AndroidApplicationConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(configure)))
    }

    fun minecraftMod(configure: MinecraftModConfiguration.() -> Unit = {}) {
        setConfigurator(MinecraftModConfigurator(MinecraftModConfiguration().apply(configure)))
    }
}
