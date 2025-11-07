package io.github.diskria.projektor.common.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.era.Beta
import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersionRange
import io.github.diskria.projektor.common.minecraft.versions.rangeTo

enum class ModLoaderType {
    FABRIC,
    LEGACY_FABRIC,
    ORNITHE,
    BABRIC,
    FORGE,
    NEOFORGE,
}

fun ModLoaderType.getSupportedVersionRange(): MinecraftVersionRange =
    when (this) {
        FABRIC -> Release.V_1_14_4..MinecraftVersion.LATEST
        LEGACY_FABRIC -> Release.V_1_3_1..Release.V_1_13_2
        ORNITHE -> MinecraftVersion.EARLIEST..Release.V_1_13_2
        BABRIC -> MinecraftVersionRange(Beta.B_1_7_3)
        FORGE -> TODO()
        NEOFORGE -> Release.V_1_20_2..MinecraftVersion.LATEST
    }
