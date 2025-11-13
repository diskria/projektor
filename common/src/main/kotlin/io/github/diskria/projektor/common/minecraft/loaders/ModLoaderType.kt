package io.github.diskria.projektor.common.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersionRange
import io.github.diskria.projektor.common.minecraft.versions.rangeTo

enum class ModLoaderType(val displayName: String, val supportedVersionRanges: List<MinecraftVersionRange>) {
    FABRIC(
        "Fabric",
        listOf(
            Release.V_1_14_4..MinecraftVersion.LATEST,
        )
    ),
    LEGACY_FABRIC(
        "LegacyFabric",
        listOf(
            MappingsType.MERGED.startMinecraftVersion..Release.V_1_13_2,
        )
    ),
    ORNITHE(
        "Ornithe",
        listOf(
            MinecraftVersion.EARLIEST..Release.V_1_13_2,
        )
    ),
    FORGE(
        "Forge",
        listOf(
            Release.V_1_20_4..MinecraftVersion.LATEST,
        )
    ),
    NEOFORGE(
        "NeoForge",
        listOf(
            Release.V_1_20_2..MinecraftVersion.LATEST,
        )
    ),
}
