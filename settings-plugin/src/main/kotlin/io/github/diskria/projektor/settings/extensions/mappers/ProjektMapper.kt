package io.github.diskria.projektor.settings.extensions.mappers

import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.projekt.ProjektType.*
import io.github.diskria.projektor.settings.configurators.*
import io.github.diskria.projektor.settings.configurators.common.Configurator

fun ProjektType.mapToConfigurator(): Configurator =
    when (this) {
        GRADLE_PLUGIN -> GradlePluginConfigurator()
        KOTLIN_LIBRARY -> KotlinLibraryConfigurator()
        ANDROID_LIBRARY -> AndroidLibraryConfigurator()
        ANDROID_APPLICATION -> AndroidApplicationConfigurator()
        MINECRAFT_MOD -> MinecraftModConfigurator()
    }
