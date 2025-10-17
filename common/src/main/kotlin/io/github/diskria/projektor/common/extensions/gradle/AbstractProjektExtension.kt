package io.github.diskria.projektor.common.extensions.gradle

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.projektor.common.configurators.IProjektConfigurator

abstract class AbstractProjektExtension<C : IProjektConfigurator> : GradleExtension() {

    private var configurator: C? = null
    private var onConfiguratorReadyCallback: ((C) -> Unit)? = null

    fun onConfiguratorReady(callback: (C) -> Unit) {
        onConfiguratorReadyCallback = callback
    }

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

    private fun notConfiguredError(): Nothing =
        gradleError("Projekt not configured!")

    private fun alreadyConfiguredError(): Nothing =
        gradleError("Projekt not configured!")
}
