package io.github.diskria.projektor.minecraft.era

import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.MinecraftVersionRange

abstract class ModdingEra(
    start: MinecraftVersion,
    end: MinecraftVersion = start
) : MinecraftVersionRange(start, end)
