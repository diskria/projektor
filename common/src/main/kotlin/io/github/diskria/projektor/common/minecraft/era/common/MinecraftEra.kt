package io.github.diskria.projektor.common.minecraft.era.common

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.common.minecraft.era.*
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion

enum class MinecraftEra(val versions: List<MinecraftVersion>, val versionPrefix: String) {

    PRE_CLASSIC(PreClassic.values().toList(), "rd-"),
    CLASSIC(Classic.values().toList(), "c"),
    INDEV(Indev.values().toList(), "in-"),
    INFDEV(Infdev.values().toList(), "inf-"),
    ALPHA(Alpha.values().toList(), "a"),
    BETA(Beta.values().toList(), "b"),
    RELEASE(Release.values().toList(), Constants.Char.EMPTY);

    fun firstVersion(): MinecraftVersion =
        versions.first()

    fun lastVersion(): MinecraftVersion =
        versions.last()

    companion object {
        fun parse(version: String): MinecraftEra =
            values().filterNot { it == RELEASE }.find { version.startsWith(it.versionPrefix) } ?: RELEASE
    }
}
