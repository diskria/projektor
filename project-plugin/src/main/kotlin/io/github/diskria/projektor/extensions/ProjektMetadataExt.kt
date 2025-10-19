package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.common.projekt.ProjektType.*
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata

fun ProjektMetadata.getId(): String =
    when (type) {
        GRADLE_PLUGIN -> TODO()
        KOTLIN_LIBRARY -> TODO()
        ANDROID_LIBRARY -> TODO()
        ANDROID_APPLICATION -> TODO()
        MINECRAFT_MOD -> TODO()
    }
