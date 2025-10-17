package io.github.diskria.projektor.configurations

import io.github.diskria.projektor.common.configurations.IKotlinLibraryConfiguration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

open class KotlinLibraryConfiguration : IKotlinLibraryConfiguration {
    var jvmTarget: JvmTarget? = null
}
