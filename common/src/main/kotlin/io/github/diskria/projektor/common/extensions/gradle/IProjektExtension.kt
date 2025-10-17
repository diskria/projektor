package io.github.diskria.projektor.common.extensions.gradle

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.projektor.common.configurations.*
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.common.projekt.ProjektType

abstract class IProjektExtension<C, GP, KL, AL, AA, MM> : GradleExtension()
        where GP : IGradlePluginConfiguration,
              KL : IKotlinLibraryConfiguration,
              AL : IAndroidLibraryConfiguration,
              AA : IAndroidApplicationConfiguration,
              MM : IMinecraftModConfiguration,
              C : IProjektConfigurator {

    private var projektType: ProjektType? = null
    private var configurator: C? = null
    private var onConfiguratorReadyCallback: ((C) -> Unit)? = null

    protected abstract fun configureGradlePlugin(configuration: GP.() -> Unit = {})
    protected abstract fun configureKotlinLibrary(configuration: KL.() -> Unit = {})
    protected abstract fun configureAndroidLibrary(configuration: AL.() -> Unit = {})
    protected abstract fun configureAndroidApplication(configuration: AA.() -> Unit = {})
    protected abstract fun configureMinecraftMod(configuration: MM.() -> Unit = {})

    fun onConfigurationReady(callback: (C) -> Unit) {
        onConfiguratorReadyCallback = callback
    }

    fun gradlePlugin(configuration: GP.() -> Unit = {}) {
        setProjektType(ProjektType.GRADLE_PLUGIN)
        configureGradlePlugin(configuration)
    }

    fun kotlinLibrary(configuration: KL.() -> Unit = {}) {
        setProjektType(ProjektType.KOTLIN_LIBRARY)
        configureKotlinLibrary(configuration)
    }

    fun androidLibrary(configuration: AL.() -> Unit = {}) {
        setProjektType(ProjektType.ANDROID_LIBRARY)
        configureAndroidLibrary(configuration)
    }

    fun androidApplication(configuration: AA.() -> Unit = {}) {
        setProjektType(ProjektType.ANDROID_APPLICATION)
        configureAndroidApplication(configuration)
    }

    fun minecraftMod(configuration: MM.() -> Unit = {}) {
        setProjektType(ProjektType.MINECRAFT_MOD)
        configureMinecraftMod(configuration)
    }

    fun getProjektType(): ProjektType =
        projektType ?: notConfiguredError()

    fun ensureConfigured() {
        configurator ?: notConfiguredError()
    }

    protected fun setConfigurator(configuration: C) {
        if (this.configurator != null) {
            alreadyConfiguredError()
        }
        this.configurator = configuration
        onConfiguratorReadyCallback?.invoke(configuration)
    }

    private fun setProjektType(projektType: ProjektType) {
        if (this.projektType != null) {
            alreadyConfiguredError()
        }
        this.projektType = projektType
    }

    private fun notConfiguredError(): Nothing =
        gradleError("Projekt not configured!")

    private fun alreadyConfiguredError(): Nothing =
        gradleError("Projekt not configured!")
}
