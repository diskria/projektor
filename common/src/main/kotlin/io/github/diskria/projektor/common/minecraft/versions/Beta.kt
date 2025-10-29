package io.github.diskria.projektor.common.minecraft.versions

import io.github.diskria.projektor.common.minecraft.era.MinecraftEra
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion

enum class Beta(private val version: String) : MinecraftVersion {

    B_1_0("1.0"),
    B_1_0_01("1.0_01"),
    B_1_0_2("1.0.2"),
    B_1_1_01("1.1_01"),
    B_1_1_02("1.1_02"),
    B_1_1_1245("1.1-1245"),
    B_1_1_1255("1.1-1255"),
    B_1_2("1.2"),
    B_1_2_01("1.2_01"),
    B_1_2_02("1.2_02"),
    B_1_3_01("1.3_01"),
    B_1_3_1731("1.3-1731"),
    B_1_3_1750("1.3-1750"),
    B_1_4_01("1.4_01"),
    B_1_4_1507("1.4-1507"),
    B_1_4_1634("1.4-1634"),
    B_1_5("1.5"),
    B_1_5_01("1.5_01"),
    B_1_5_02("1.5_02"),
    B_1_6_PRE_TRAILER("1.6-pre-trailer"),
    B_1_6_TEST_BUILD_3("1.6-tb3"),
    B_1_6("1.6"),
    B_1_6_1("1.6.1"),
    B_1_6_2("1.6.2"),
    B_1_6_3("1.6.3"),
    B_1_6_4("1.6.4"),
    B_1_6_5("1.6.5"),
    B_1_6_6("1.6.6"),
    B_1_7("1.7"),
    B_1_7_01("1.7_01"),
    B_1_7_2("1.7_2"),
    B_1_7_3("1.7.3"),
    B_1_8_PRE1_201109081459("1.8-pre1-201109081459"),
    B_1_8_PRE1_201109091357("1.8-pre1-201109091357"),
    B_1_8_PRE2("1.8-pre2"),
    B_1_8("1.8"),
    B_1_8_1("1.8.1"),
    B_1_9_PRE1("1.9-pre1"),
    B_1_9_PRE2("1.9-pre2"),
    B_1_9_PRE3_201110061350("1.9-pre3-201110061350"),
    B_1_9_PRE3_201110061402("1.9-pre3-201110061402"),
    B_1_9_PRE4_201110131434("1.9-pre4-201110131434"),
    B_1_9_PRE4_201110131440("1.9-pre4-201110131440"),
    B_1_9_PRE5("1.9-pre5"),
    B_1_9_PRE6("1.9-pre6");

    override fun getEra(): MinecraftEra = MinecraftEra.BETA

    override fun getEnumVersion(): String = version
}
