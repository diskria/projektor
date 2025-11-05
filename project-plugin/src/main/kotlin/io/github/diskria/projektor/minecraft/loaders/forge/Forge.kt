package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.extensions.forge
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

data object Forge : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
            val configPath = "META-INF".appendPath(fileName("mods", Constants.File.Extension.TOML))
            forge {

            }
        }
    }
}
