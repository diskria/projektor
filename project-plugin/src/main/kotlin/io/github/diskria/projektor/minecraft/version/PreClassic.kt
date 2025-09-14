package io.github.diskria.projektor.minecraft.version

import io.github.diskria.projektor.minecraft.era.MinecraftEra

enum class PreClassic(private val version: String) : MinecraftVersion {
    RD_132211_LAUNCHER("132211-launcher"),
    RD_132328_LAUNCHER("132328-launcher"),
    RD_160052_LAUNCHER("160052-launcher"),
    RD_161348_LAUNCHER("161348-launcher");

    override fun getEra(): MinecraftEra = MinecraftEra.PRE_CLASSIC

    override fun getVersionInternal(): String = version
}
