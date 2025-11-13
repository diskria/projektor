package io.github.diskria.projektor.minecraft.helpers

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.projekt.MinecraftMod

abstract class AbstractMinecraftPresetHelper {

    open val argumentSeparator: Char = Constants.Char.EQUAL_SIGN

    abstract fun buildPreset(mod: MinecraftMod): String

    protected fun buildArgument(name: String, value: String): String =
        name + argumentSeparator + value

    protected fun buildArgument(name: String, value: Boolean): String =
        buildArgument(name, value.toString())

    protected fun buildArgument(name: String, value: Float): String =
        buildArgument(name, value.toString())

    protected fun buildArgument(name: String, value: Int): String =
        buildArgument(name, value.toString())
}
