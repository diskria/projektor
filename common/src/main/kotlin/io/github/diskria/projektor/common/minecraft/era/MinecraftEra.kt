package io.github.diskria.projektor.common.minecraft.era

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.common.minecraft.versions.*
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion

enum class MinecraftEra(
    val versions: List<MinecraftVersion>,
    val versionPrefix: String = Constants.Char.EMPTY,
) {
    PRE_CLASSIC(PreClassic.entries.toList(), "rd-"),
    CLASSIC(Classic.entries.toList(), "c"),
    INDEV(Indev.entries.toList(), "in-"),
    INFDEV(Infdev.entries.toList(), "inf-"),
    ALPHA(Alpha.entries.toList(), "a"),
    BETA(Beta.entries.toList(), "b"),
    RELEASE(Release.entries.toList());

    companion object {
        fun parse(version: String): MinecraftEra =
            MinecraftEra.entries.filterNot { it == RELEASE }.find { version.startsWith(it.versionPrefix) } ?: RELEASE
    }
}

fun MinecraftEra.firstVersion(): MinecraftVersion =
    versions.first()

fun MinecraftEra.lastVersion(): MinecraftVersion =
    versions.last()
