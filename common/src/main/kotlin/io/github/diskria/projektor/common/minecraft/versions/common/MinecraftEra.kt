package io.github.diskria.projektor.common.minecraft.versions.common

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.common.minecraft.versions.Beta
import io.github.diskria.projektor.common.minecraft.versions.Release

enum class MinecraftEra(
    val versions: List<MinecraftVersion>,
    val versionPrefix: String,
) {
    BETA(Beta.entries.toList(), "b"),
    RELEASE(Release.entries.toList(), Constants.Char.EMPTY);

    companion object {
        fun parse(version: String): MinecraftEra =
            entries.filterNot { it == RELEASE }.find { version.startsWith(it.versionPrefix) } ?: RELEASE
    }
}

fun MinecraftEra.firstVersion(): MinecraftVersion =
    versions.first()

fun MinecraftEra.lastVersion(): MinecraftVersion =
    versions.last()
