package io.github.diskria.projektor.extensions.kotlin.mappers

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun JvmTarget.toInt(): Int =
    if (this == JvmTarget.JVM_1_8) 8
    else target.toInt()

fun Int.toJvmTarget(): JvmTarget =
    if (this == 8) JvmTarget.JVM_1_8
    else if (this == 25) JvmTarget.JVM_24 // TODO remove after Kotlin 2.3.0 release
    else JvmTarget.fromTarget(toString())
