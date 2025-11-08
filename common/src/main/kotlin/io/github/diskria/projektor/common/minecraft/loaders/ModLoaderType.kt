package io.github.diskria.projektor.common.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersionRange
import io.github.diskria.projektor.common.minecraft.versions.rangeTo

enum class ModLoaderType {
    FABRIC,
    LEGACY_FABRIC,
    ORNITHE,
    FORGE,
    NEOFORGE,
}

fun ModLoaderType.getSupportedVersionRanges(): List<MinecraftVersionRange> =
    when (this) {
        FABRIC -> listOf(
            Release.V_1_14_4..MinecraftVersion.LATEST,
        )

        LEGACY_FABRIC -> listOf(
            Release.V_1_3_1..Release.V_1_13_2,
        )

        ORNITHE -> listOf(
            MinecraftVersion.EARLIEST..Release.V_1_13_2,
        )

        FORGE -> listOf(
            Release.V_1_17_1..MinecraftVersion.LATEST,
        )

        NEOFORGE -> listOf(
            Release.V_1_20_2..MinecraftVersion.LATEST,
        )
    }
