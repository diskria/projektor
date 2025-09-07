package io.github.diskria.projektor.minecraft.era

import io.github.diskria.projektor.minecraft.version.*
import io.github.diskria.utils.kotlin.Constants

enum class MinecraftEra(val versions: List<MinecraftVersion>, val versionPrefix: String = Constants.Char.EMPTY) {

    PRE_CLASSIC(PreClassic.entries.toList(), "rd-"),
    CLASSIC(Classic.entries.toList(), "c"),
    INDEV(Indev.entries.toList(), "in-"),
    INFDEV(Infdev.entries.toList(), "inf-"),
    ALPHA(Alpha.entries.toList(), "a"),
    BETA(Beta.entries.toList(), "b"),
    RELEASE(Release.entries.toList());

    fun firstVersion(): MinecraftVersion =
        versions.first()
}
