package io.github.diskria.projektor.common.minecraft.era

import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange

abstract class ModdingEra(start: MinecraftVersion, end: MinecraftVersion = start) : MinecraftVersionRange(start, end)
