package io.github.diskria.projektor.configurations

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

open class KotlinLibraryConfiguration {
    var jvmTarget: JvmTarget? = null
    var javaVersion: Int? = null
}
