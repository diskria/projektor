package io.github.diskria.projektor.minecraft

import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.extensions.appendPrefix
import io.github.diskria.utils.kotlin.extensions.common.fileName
import io.github.diskria.utils.kotlin.extensions.common.modifyIf

enum class ModLoader(val logicalName: String) {
    FABRIC("fabric"),
    QUILT("quilt"),
    FORGE("forge"),
    NEOFORGE("neoforge");
}

fun ModLoader.getConfigFilePath(): String =
    when (this) {
        ModLoader.FABRIC, ModLoader.QUILT -> fileName(logicalName, "mod", Constants.File.Extension.JSON)

        ModLoader.FORGE, ModLoader.NEOFORGE -> "META-INF/" + fileName("mods", Constants.File.Extension.TOML)
            .modifyIf(this == ModLoader.NEOFORGE) { it.appendPrefix(logicalName + Constants.Char.DOT) }
    }
