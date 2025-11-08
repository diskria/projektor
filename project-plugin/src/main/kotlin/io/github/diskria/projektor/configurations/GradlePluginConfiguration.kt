package io.github.diskria.projektor.configurations

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

open class GradlePluginConfiguration {
    var isSettingsPlugin: Boolean = false
    var jvmTarget: JvmTarget? = null
}
