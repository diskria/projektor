package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

data object NeoForge : ModLoader {

    override fun getConfigFilePath(): String =
        "META-INF".appendPath(fileName("mods", Constants.File.Extension.TOML))

    override fun configure(project: Project, mod: MinecraftMod) = with(project) {
        TODO()
    }
}
