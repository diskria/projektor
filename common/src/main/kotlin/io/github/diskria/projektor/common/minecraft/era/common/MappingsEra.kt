package io.github.diskria.projektor.common.minecraft.era.common

import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.compareTo

enum class MappingsEra(val startMinecraftVersion: MinecraftVersion) {

    CLIENT(MinecraftVersion.EARLIEST),
    SPLIT(MinecraftEra.BETA.firstVersion()),
    MERGED(Release.V_1_3_1);

    companion object {
        fun of(minecraftVersion: MinecraftVersion): MappingsEra =
            values()
                .sortedWith(compareByDescending(MinecraftVersion.COMPARATOR) { it.startMinecraftVersion })
                .first { minecraftVersion >= it.startMinecraftVersion }
    }
}
