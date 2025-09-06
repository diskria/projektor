package io.github.diskria.projektor.minecraft.version

enum class Classic(private val version: String) : MinecraftVersion {

    V_0_30_C_RENEW("30-c-renew"),
    V_0_30_C("30-c"),
    V_0_30_S("30-s"),
    V_0_29_02("29_02"),
    V_0_29_01("29_01"),
    V_0_29("29"),
    V_0_28_01("28_01"),
    V_0_27_ST("27_st"),
    V_0_25_05_ST("25_05_st"),
    V_0_24_ST_03("24_st_03"),
    V_0_0_23A_01("0.23a_01"),
    V_0_0_22A_05("0.22a_05"),
    V_0_0_21A("0.21a"),
    V_0_0_20A_02("0.20a_02"),
    V_0_0_20A_01("0.20a_01"),
    V_0_0_19A_06("0.19a_06"),
    V_0_0_19A_04("0.19a_04"),
    V_0_0_18A_02("0.18a_02"),
    V_0_0_17A("0.17a"),
    V_0_0_16A_02("0.16a_02"),
    V_0_0_15A("0.15a"),
    V_0_0_14A_08("0.14a_08"),
    V_0_0_13A_03_LAUNCHER("0.13a_03-launcher"),
    V_0_0_13A_03("0.13a_03"),
    V_0_0_13A_LAUNCHER("0.13a-launcher"),
    V_0_0_12A_03("0.12a_03"),
    V_0_0_11A_LAUNCHER("0.11a-launcher");

    override fun getVersion(): String = "c0.$version"
}
