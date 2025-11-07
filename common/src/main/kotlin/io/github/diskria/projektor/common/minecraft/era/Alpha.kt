package io.github.diskria.projektor.common.minecraft.era

import io.github.diskria.projektor.common.minecraft.era.common.MinecraftEra
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion

enum class Alpha(private val version: String) : MinecraftVersion {

    A_1_0_5_01("1.0.5_01"),
    A_1_0_11("1.0.11"),
    A_1_0_15("1.0.15"),
    A_1_0_16("1.0.16"),
    A_1_0_17_04("1.0.17_04"),
    A_1_1_2_01("1.1.2_01"),
    A_1_2_0_02("1.2.0_02"),
    A_1_2_1_01("1.2.1_01"),
    A_1_2_2_1624("1.2.2-1624"),
    A_1_2_3_04("1.2.3_04"),
    A_1_2_4_01("1.2.4_01"),
    A_1_2_5("1.2.5"),
    A_1_2_6("1.2.6");

    override fun getEra(): MinecraftEra = MinecraftEra.ALPHA

    override fun getEnumVersion(): String = version
}
