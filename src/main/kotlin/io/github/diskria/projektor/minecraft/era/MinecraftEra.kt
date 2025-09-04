package io.github.diskria.projektor.minecraft.era

import io.github.diskria.utils.kotlin.Constants

enum class MinecraftEra(val versionPrefix: String = Constants.Char.EMPTY) {
    RUBY_DUNG("rd-"),
    CLASSIC("c"),
    INDEV("in-"),
    INFDEV("inf-"),
    ALPHA("a"),
    BETA("b"),
    RELEASE;
}
