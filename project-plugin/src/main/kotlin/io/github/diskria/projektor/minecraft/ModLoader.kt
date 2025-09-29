package io.github.diskria.projektor.minecraft

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.mappers.getName

enum class ModLoader {
    FABRIC,
    QUILT,
    FORGE,
    NEOFORGE,
}

fun ModLoader.getConfigFilePath(): String =
    when (this) {
        ModLoader.FABRIC, ModLoader.QUILT -> {
            fileName(getName(), "mod", Constants.File.Extension.JSON)
        }

        ModLoader.FORGE, ModLoader.NEOFORGE -> {
            "META-INF/" + fileName("mods", Constants.File.Extension.TOML)
                .modifyIf(this == ModLoader.NEOFORGE) { it.appendPrefix(getName() + Constants.Char.DOT) }
        }
    }
