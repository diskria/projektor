package io.github.diskria.projektor.minecraft.version

import io.github.diskria.kotlin.utils.extensions.common.failWithDetails
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.minecraft.era.MinecraftEra

interface MinecraftVersion {

    fun getEra(): MinecraftEra

    fun getVersionInternal(): String

    companion object {
        fun of(version: String): MinecraftVersion {
            val era = MinecraftEra.entries
                .filterNot { it == MinecraftEra.RELEASE }
                .find { version.startsWith(it.versionPrefix) }
                ?: MinecraftEra.RELEASE
            return era.versions.find { version == it.getVersionInternal() }
                ?: failWithDetails("Unknown Minecraft version") {
                    val era by era.name.autoNamedProperty()
                    val version by version.autoNamedProperty()
                    listOf(era, version)
                }
        }
    }
}

fun MinecraftVersion.asEnum(): Enum<*> {
    require(this is Enum<*>) { "Only enums can implement MinecraftVersion" }
    return this
}

fun MinecraftVersion.getOrdinal(): Int =
    asEnum().ordinal

operator fun MinecraftVersion.compareTo(other: MinecraftVersion): Int =
    when {
        getEra() != other.getEra() -> getEra().ordinal - other.getEra().ordinal
        else -> getOrdinal() - other.getOrdinal()
    }

fun MinecraftVersion.asString(): String =
    getEra().versionPrefix + getVersionInternal()

fun MinecraftVersion.getMinJavaVersion(): Int =
    when {
        this >= Release.V_1_20_5 -> 21
        this >= Release.V_1_18 -> 17
        this >= Release.V_1_17 -> 16
        this >= Release.V_1_12 -> 8
        this >= Release.V_1_6_1 || this <= PreClassic.RD_132328_LAUNCHER -> 6
        else -> 5
    }

fun MinecraftVersion.getMinDataPackFormat(): Int =
    when {
        this >= Release.V_1_21_9 -> 86
        this >= Release.V_1_21_7 -> 81
        this >= Release.V_1_21_6 -> 80
        this >= Release.V_1_21_5 -> 71
        this >= Release.V_1_21_4 -> 61
        this >= Release.V_1_21_2 -> 57
        this >= Release.V_1_21 -> 48
        this >= Release.V_1_20_5 -> 41
        this >= Release.V_1_20_3 -> 26
        this >= Release.V_1_20_2 -> 18
        this >= Release.V_1_20 -> 15
        this >= Release.V_1_19_4 -> 12
        this >= Release.V_1_19 -> 10
        this >= Release.V_1_18_2 -> 9
        this >= Release.V_1_18 -> 8
        this >= Release.V_1_17 -> 7
        this >= Release.V_1_16_2 -> 6
        this >= Release.V_1_15 -> 5
        this >= Release.V_1_13 -> 4
        else -> failWithDetails("Unsupported version") {
            val minecraftVersion by this.autoNamedProperty()
            val minSupportedVersion by Release.V_1_13.autoNamedProperty()
            listOf(minecraftVersion, minSupportedVersion)
        }
    }

fun MinecraftVersion.getMinResourcePackFormat(): Int =
    when {
        this >= Release.V_1_21_7 -> 64
        this >= Release.V_1_21_6 -> 63
        this >= Release.V_1_21_5 -> 55
        this >= Release.V_1_21_4 -> 46
        this >= Release.V_1_21_2 -> 42
        this >= Release.V_1_21 -> 34
        this >= Release.V_1_20_5 -> 32
        this >= Release.V_1_20_3 -> 22
        this >= Release.V_1_20 -> 15
        this >= Release.V_1_19_4 -> 13
        this >= Release.V_1_19_3 -> 12
        this >= Release.V_1_19 -> 9
        this >= Release.V_1_18 -> 8
        this >= Release.V_1_17 -> 7
        this >= Release.V_1_16_2 -> 6
        this >= Release.V_1_15 -> 5
        this >= Release.V_1_13 -> 4
        this >= Release.V_1_11 -> 3
        this >= Release.V_1_9 -> 2
        this >= Release.V_1_6_1 -> 1
        else -> failWithDetails("Unsupported version") {
            val minecraftVersion by this.autoNamedProperty()
            val minSupportedVersion by Release.V_1_6_1.autoNamedProperty()
            listOf(minecraftVersion, minSupportedVersion)
        }
    }
