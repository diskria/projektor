package io.github.diskria.projektor.minecraft.version

enum class PreClassic(private val version: String) : MinecraftVersion {

    V_161348("161348"),
    V_160052("160052"),
    V_132328("132328"),
    V_132211("132211");

    override fun getVersion(): String = "rd-$version-launcher"
}
