package io.github.diskria.projektor.configurations

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

open class GradlePluginConfiguration {
    var isSettingsPlugin: Boolean = false
    var javaVersion: Int? = null
    var jvmTarget: JvmTarget? = null
}
