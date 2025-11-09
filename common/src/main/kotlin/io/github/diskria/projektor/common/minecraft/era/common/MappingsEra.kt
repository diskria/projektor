package io.github.diskria.projektor.common.minecraft.era.common

import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.compareTo

enum class MappingsEra(val startMinecraftVersion: MinecraftVersion, val sides: List<ModSide>) {

    CLIENT(
        MinecraftVersion.EARLIEST,
        listOf(ModSide.CLIENT),
    ),
    SPLIT(
        MinecraftEra.BETA.firstVersion(),
        listOf(ModSide.CLIENT, ModSide.SERVER),
    ),
    MERGED(
        Release.V_1_3_1,
        listOf(ModSide.CLIENT, ModSide.SERVER),
    );

    companion object {
        fun of(minecraftVersion: MinecraftVersion): MappingsEra =
            values()
                .sortedWith(compareByDescending(MinecraftVersion.COMPARATOR) { it.startMinecraftVersion })
                .first { minecraftVersion >= it.startMinecraftVersion }
    }
}
