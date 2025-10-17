package io.github.diskria.projektor.common.extensions.gradle

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension

open class ConfigurationExtension<T> : GradleExtension() {

    private var configuration: T? = null
    private var onConfigurationReadyCallback: ((T) -> Unit)? = null

    fun onConfigurationReady(callback: (T) -> Unit) {
        onConfigurationReadyCallback = callback
    }

    fun ensureConfigured(): T =
        configuration ?: gradleError("Projekt not configured!")

    protected fun configure(configuration: T) {
        if (this.configuration != null) {
            gradleError("Projekt already configured!")
        }
        this.configuration = configuration
        onConfigurationReadyCallback?.invoke(configuration)
    }
}
