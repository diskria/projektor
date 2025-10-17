package io.github.diskria.projektor.extensions.gradle

import io.github.diskria.projektor.common.extensions.gradle.IProjektExtension
import io.github.diskria.projektor.configurations.*
import io.github.diskria.projektor.configurators.*
import io.github.diskria.projektor.configurators.common.ProjectConfigurator

open class ProjektExtension : IProjektExtension<
        ProjectConfigurator<*>,
        GradlePluginConfiguration,
        KotlinLibraryConfiguration,
        AndroidLibraryConfiguration,
        AndroidApplicationConfiguration,
        MinecraftModConfiguration,
        >() {

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
