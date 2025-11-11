package io.github.diskria.projektor.common.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersionRange
import io.github.diskria.projektor.common.minecraft.versions.rangeTo

enum class ModLoaderType(val supportedVersionRanges: List<MinecraftVersionRange>) {
    FABRIC(
        listOf(
            Release.V_1_14_4..MinecraftVersion.LATEST,
        )
    ),
    LEGACY_FABRIC(
        listOf(
            MappingsType.MERGED.startMinecraftVersion..Release.V_1_13_2,
        )
    ),
    ORNITHE(
        listOf(
            MinecraftVersion.EARLIEST..Release.V_1_13_2,
        )
    ),
    FORGE(
        listOf(
            Release.V_1_17_1..MinecraftVersion.LATEST,
        )
    ),
    NEOFORGE(
        listOf(
            Release.V_1_20_2..MinecraftVersion.LATEST,
        )
    ),
}
