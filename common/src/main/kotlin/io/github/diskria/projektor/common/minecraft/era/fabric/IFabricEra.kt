package io.github.diskria.projektor.common.minecraft.era.fabric

import io.github.diskria.projektor.common.minecraft.era.ModdingEra
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion

sealed class IFabricEra(start: MinecraftVersion, end: MinecraftVersion = start) : ModdingEra(start, end)
