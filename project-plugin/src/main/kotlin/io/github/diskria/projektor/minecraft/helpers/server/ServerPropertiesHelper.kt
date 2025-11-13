package io.github.diskria.projektor.minecraft.helpers.server

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.minecraft.helpers.AbstractMinecraftPresetHelper
import io.github.diskria.projektor.projekt.MinecraftMod

object ServerPropertiesHelper : AbstractMinecraftPresetHelper() {

    val FILE_NAME: String = fileName(ModSide.SERVER.getName(), Constants.File.Extension.PROPERTIES)

    override fun buildPreset(mod: MinecraftMod): String =
        buildString {
            appendLine(buildArgument("online-mode", false))
            appendLine(buildArgument("allow-flight", true))
            appendLine(buildArgument("enforce-secure-profile", false))
            appendLine(buildArgument("op-permission-level", 4))
        }
}
