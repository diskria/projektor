package io.github.diskria.projektor.minecraft.version

import io.github.diskria.projektor.minecraft.era.MinecraftEra

interface MinecraftVersion {
    fun getEra(): MinecraftEra
    fun getVersion(): String
}
