package io.github.diskria.projektor.minecraft.version

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.common.failWithDetails
import io.github.diskria.kotlin.utils.extensions.previousEnumOrNull
import io.github.diskria.kotlin.utils.extensions.previousOrNull
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.minecraft.era.MinecraftEra
import io.github.diskria.projektor.minecraft.era.firstVersion
import io.github.diskria.projektor.minecraft.era.lastVersion

interface MinecraftVersion {

    fun getEra(): MinecraftEra

    fun getVersionInternal(): String

    companion object {
        val LATEST: MinecraftVersion = MinecraftEra.entries.last().lastVersion()
        val EARLIEST: MinecraftVersion = MinecraftEra.entries.first().firstVersion()

        fun of(version: String): MinecraftVersion =
            MinecraftEra.of(version).versions.find { version == it.getVersionInternal() }
                ?: failWithDetails("Unknown Minecraft version") {
                    val version by version.autoNamedProperty()
                    listOf(version)
                }

        fun of(enum: Enum<*>): MinecraftVersion =
            enum as? MinecraftVersion
                ?: gradleError("Expected enum implementing MinecraftVersion, but got ${enum::class.className()}")
    }
}

fun MinecraftVersion.asEnum(): Enum<*> =
    this as? Enum<*>
        ?: gradleError("Expected MinecraftVersion implemented by enum, but got ${this::class.className()}")

fun MinecraftVersion.getOrdinal(): Int =
    asEnum().ordinal

operator fun MinecraftVersion.compareTo(other: MinecraftVersion): Int {
    val era = getEra()
    val otherEra = other.getEra()
    return when {
        era == otherEra -> getOrdinal() - other.getOrdinal()
        else -> era.ordinal - otherEra.ordinal
    }
}

fun List<MinecraftVersion>.sorted(): List<MinecraftVersion> =
    sortedWith { previous, next -> previous.compareTo(next) }

fun MinecraftVersion.previousOrNull(): MinecraftVersion? =
    asEnum().previousEnumOrNull()?.let { MinecraftVersion.of(it) } ?: getEra().previousOrNull()?.lastVersion()

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
        this >= Release.V_1_21_9 -> 88
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
            val minVersion by Release.V_1_13.autoNamedProperty()
            listOf(minecraftVersion, minVersion)
        }
    }

fun MinecraftVersion.getMinResourcePackFormat(): Int =
    when {
        this >= Release.V_1_21_9 -> 69
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
            val minVersion by Release.V_1_6_1.autoNamedProperty()
            listOf(minecraftVersion, minVersion)
        }
    }
