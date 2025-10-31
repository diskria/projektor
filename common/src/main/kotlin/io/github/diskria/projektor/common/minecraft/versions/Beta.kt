package io.github.diskria.projektor.common.minecraft.versions

import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftEra
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion

enum class Beta(private val version: String) : MinecraftVersion {

    B_1_0_01("1.0_01"),
    B_1_1_02("1.1_02"),
    B_1_2_01("1.2_01"),
    B_1_4_01("1.4_01"),
    B_1_5_01("1.5_01"),
    B_1_6("1.6"),
    B_1_6_1("1.6.1"),
    B_1_6_2("1.6.2"),
    B_1_6_3("1.6.3"),
    B_1_6_4("1.6.4"),
    B_1_6_5("1.6.5"),
    B_1_6_6("1.6.6"),
    B_1_7_01("1.7_01"),
    B_1_7_3("1.7.3"),
    B_1_8("1.8"),
    B_1_8_1("1.8.1");

    override fun getEra(): MinecraftEra = MinecraftEra.BETA

    override fun getEnumVersion(): String = version
}
