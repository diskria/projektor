package io.github.diskria.projektor.minecraft.helpers.server

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.serialization.serializeToJson
import io.github.diskria.projektor.minecraft.helpers.AbstractMinecraftPresetHelper
import io.github.diskria.projektor.minecraft.helpers.server.operators.OperatorEntry
import io.github.diskria.projektor.minecraft.helpers.server.operators.Operators
import io.github.diskria.projektor.projekt.MinecraftMod

object ServerOperatorsHelper : AbstractMinecraftPresetHelper() {

    val FILE_NAME: String = fileName("ops", Constants.File.Extension.JSON)

    override fun buildPreset(mod: MinecraftMod): String =
        Operators(
            listOf(
                OperatorEntry(
                    uuid = mod.developerOfflineUUID.toString(),
                    name = mod.developerUsername,
                )
            )
        ).serializeToJson()
}
