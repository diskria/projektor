package io.github.diskria.projektor.common.minecraft.versions

import io.github.diskria.kotlin.utils.extensions.common.failWithDetails
import io.github.diskria.kotlin.utils.extensions.previousEnumOrNull
import io.github.diskria.kotlin.utils.extensions.previousOrNull
import io.github.diskria.kotlin.utils.extensions.toSemverOrNull
import io.github.diskria.projektor.common.minecraft.era.common.*
import io.github.diskria.projektor.common.minecraft.sync.packs.DataPackFormatSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.packs.ResourcePackFormatSynchronizer
import org.gradle.api.Project

interface MinecraftVersion {

    fun getEra(): MinecraftEra

    fun getEnumVersion(): String

    companion object {
        val EARLIEST: MinecraftVersion = MinecraftEra.values().first().firstVersion()
        val LATEST: MinecraftVersion = MinecraftEra.values().last().lastVersion()

        val COMPARATOR: Comparator<MinecraftVersion> = Comparator { before, after -> before.compareTo(after) }

        fun parse(version: String): MinecraftVersion =
            parseOrNull(version) ?: failWithDetails("Failed to parse Minecraft version $version")

        fun parseOrNull(version: String): MinecraftVersion? {
            val era = MinecraftEra.parse(version)
            val normalizedVersion = version.toSemverOrNull()?.toVersion(dropZeroPatch = true) ?: version
            return era.versions.find { normalizedVersion == era.versionPrefix + it.getEnumVersion() }
        }

        fun of(enum: Enum<*>): MinecraftVersion =
            enum as MinecraftVersion
    }
}

operator fun MinecraftVersion.compareTo(other: MinecraftVersion): Int {
    val era = getEra()
    val otherEra = other.getEra()
    return when {
        era == otherEra -> getOrdinal() - other.getOrdinal()
        else -> era.ordinal - otherEra.ordinal
    }
}

operator fun MinecraftVersion.rangeTo(other: MinecraftVersion): MinecraftVersionRange =
    MinecraftVersionRange(this, other)

fun MinecraftVersion.asEnum(): Enum<*> =
    this as Enum<*>

fun MinecraftVersion.asString(): String =
    getEra().versionPrefix + getEnumVersion()

fun MinecraftVersion.getOrdinal(): Int =
    asEnum().ordinal

fun MinecraftVersion.previousOrNull(): MinecraftVersion? =
    asEnum().previousEnumOrNull()?.let { MinecraftVersion.of(it) } ?: getEra().previousOrNull()?.lastVersion()

val MinecraftVersion.minJavaVersion: Int
    get() = JavaCompatibility.getMinJavaVersion(this)

val MinecraftVersion.mappingsEra: MappingsEra
    get() = MappingsEra.of(this)

fun MinecraftVersion.getDataPackFormat(project: Project): String =
    DataPackFormatSynchronizer.getLatestVersion(project, this)

fun MinecraftVersion.getResourcePackFormat(project: Project): String =
    ResourcePackFormatSynchronizer.getLatestVersion(project, this)
