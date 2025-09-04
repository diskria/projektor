package io.github.diskria.projektor.minecraft.era.fabric

import io.github.diskria.projektor.minecraft.era.ModdingEra
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.MinecraftVersionRange

sealed class IFabricEra(start: MinecraftVersion, end: MinecraftVersion = start) : ModdingEra(start, end) {
    constructor(start: MinecraftVersionRange, end: MinecraftVersionRange) : this(start.start, end.end)
}
