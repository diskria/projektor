package io.github.diskria.projektor.common.minecraft.era

import io.github.diskria.projektor.common.minecraft.era.common.MinecraftEra
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion

enum class Infdev(private val version: String) : MinecraftVersion {

    INFDEV_20100618("20100618");

    override fun getEra(): MinecraftEra = MinecraftEra.INFDEV

    override fun getEnumVersion(): String = version
}
