package io.github.diskria.projektor.common.minecraft.versions.common

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.common.failWithDetails
import io.github.diskria.kotlin.utils.extensions.previousEnumOrNull
import io.github.diskria.kotlin.utils.extensions.previousOrNull
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.minecraft.era.MinecraftEra
import io.github.diskria.projektor.common.minecraft.era.firstVersion
import io.github.diskria.projektor.common.minecraft.era.lastVersion
import io.github.diskria.projektor.common.minecraft.versions.Release

interface MinecraftVersion {

    fun getEra(): MinecraftEra

    fun getEnumVersion(): String

    companion object {
        val LATEST: MinecraftVersion = MinecraftEra.entries.last().lastVersion()
        val EARLIEST: MinecraftVersion = MinecraftEra.entries.first().firstVersion()

        val COMPARATOR: Comparator<MinecraftVersion> = Comparator { previous, next ->
            previous.compareTo(next)
        }

        fun parse(version: String): MinecraftVersion =
            parseOrNull(version) ?: failWithDetails("Unknown Minecraft version") {
                val version by version.autoNamedProperty()
                listOf(version)
            }

        fun parseOrNull(version: String): MinecraftVersion? =
            MinecraftEra.parse(version).versions.find { version == it.getEnumVersion() }

        fun of(enum: Enum<*>): MinecraftVersion =
            enum as? MinecraftVersion
                ?: gradleError("Expected enum implementing MinecraftVersion, but got ${enum::class.className()}")
    }
}

fun MinecraftVersion.asEnum(): Enum<*> =
    this as? Enum<*>
        ?: gradleError("Expected MinecraftVersion implemented by enum, but got ${this::class.className()}")

fun MinecraftVersion.asString(): String =
    getEra().versionPrefix + getEnumVersion()

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

fun MinecraftVersion.previousOrNull(): MinecraftVersion? =
    asEnum().previousEnumOrNull()?.let { MinecraftVersion.of(it) } ?: getEra().previousOrNull()?.lastVersion()

private val JAVA_REQUIREMENTS: Map<MinecraftVersion, Int> = mapOf(
    Release.V_1_20_5 to 21,
    Release.V_1_18 to 17,
    Release.V_1_17 to 16,
    Release.V_1_12 to 8,
    Release.V_1_6_1 to 6,
    MinecraftVersion.EARLIEST to 5,
)

fun MinecraftVersion.getMinJavaVersion(): Int =
    JAVA_REQUIREMENTS
        .entries
        .sortedWith(compareByDescending(MinecraftVersion.COMPARATOR) { it.key })
        .first { this >= it.key }
        .value
