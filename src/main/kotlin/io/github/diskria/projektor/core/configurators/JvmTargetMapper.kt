package io.github.diskria.projektor.core.configurators

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal fun JvmTarget.toVersion(): Int =
    if (this == JvmTarget.JVM_1_8) 8
    else target.toInt()

internal fun jvmTargetOf(version: Int): JvmTarget =
    if (version == 8) JvmTarget.JVM_1_8
    else JvmTarget.fromTarget(version.toString())
