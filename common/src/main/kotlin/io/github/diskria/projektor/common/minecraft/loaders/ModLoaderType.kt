package io.github.diskria.projektor.common.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.common.minecraft.versions.Release
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange

enum class ModLoaderType {
    FABRIC,
    ORNITHE,
    QUILT,
    FORGE,
    NEOFORGE,
}

fun ModLoaderType.getSupportedVersionRange(): MinecraftVersionRange =
    when (this) {
        FABRIC -> MinecraftVersionRange(Release.V_1_14_3, MinecraftVersion.LATEST)
        ORNITHE -> MinecraftVersionRange(MinecraftVersion.EARLIEST, Release.V_1_13_2)
        QUILT -> TODO()
        FORGE -> TODO()
        NEOFORGE -> MinecraftVersionRange(Release.V_1_20_2, MinecraftVersion.LATEST)
    }
