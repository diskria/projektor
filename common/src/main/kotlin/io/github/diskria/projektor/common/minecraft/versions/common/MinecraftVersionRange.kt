package io.github.diskria.projektor.common.minecraft.versions.common

open class MinecraftVersionRange(val min: MinecraftVersion, val max: MinecraftVersion = min) {

    fun expand(): List<MinecraftVersion> {
        val minEra = min.getEra()
        val maxEra = max.getEra()

        return MinecraftEra.entries.filter { it in minEra..maxEra }.flatMap { era ->
            val versions = era.versions
            when {
                minEra == maxEra -> versions.filter { it >= min && it <= max }
                era == minEra -> versions.filter { it >= min }
                era == maxEra -> versions.filter { it <= max }
                else -> versions
            }
        }.sortedWith(MinecraftVersion.COMPARATOR)
    }

    fun includesVersion(minecraftVersion: MinecraftVersion): Boolean =
        minecraftVersion >= min && minecraftVersion <= max
}
