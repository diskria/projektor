package io.github.diskria.projektor.settings.extensions.mappers

import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.projekt.ProjektType.*
import io.github.diskria.projektor.settings.projekt.*
import io.github.diskria.projektor.settings.projekt.common.Projekt

fun ProjektType.mapToModel(): Projekt =
    when (this) {
        GRADLE_PLUGIN -> GradlePlugin
        KOTLIN_LIBRARY -> KotlinLibrary
        ANDROID_LIBRARY -> AndroidLibrary
        ANDROID_APPLICATION -> AndroidApplication
        MINECRAFT_MOD -> MinecraftMod
    }

fun Projekt.mapToEnum(): ProjektType =
    when (this) {
        GradlePlugin -> GRADLE_PLUGIN
        KotlinLibrary -> KOTLIN_LIBRARY
        AndroidLibrary -> ANDROID_LIBRARY
        AndroidApplication -> ANDROID_APPLICATION
        MinecraftMod -> MINECRAFT_MOD
        else -> failWithUnsupportedType(this::class)
    }
