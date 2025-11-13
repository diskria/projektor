package io.github.diskria.projektor.minecraft.helpers.server

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.minecraft.helpers.AbstractMinecraftPresetHelper
import io.github.diskria.projektor.projekt.MinecraftMod

object EulaHelper : AbstractMinecraftPresetHelper() {

    private const val EULA_NAME: String = "eula"

    val FILE_NAME: String = fileName(EULA_NAME, Constants.File.Extension.TXT)

    override fun buildPreset(mod: MinecraftMod): String =
        buildString {
            appendLine(buildArgument(EULA_NAME, true))
        }
}
