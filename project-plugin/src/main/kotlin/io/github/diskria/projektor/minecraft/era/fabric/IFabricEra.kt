package io.github.diskria.projektor.minecraft.era.fabric

import io.github.diskria.projektor.minecraft.era.ModdingEra
import io.github.diskria.projektor.minecraft.version.MinecraftVersion

sealed class IFabricEra(start: MinecraftVersion, end: MinecraftVersion = start) : ModdingEra(start, end)
