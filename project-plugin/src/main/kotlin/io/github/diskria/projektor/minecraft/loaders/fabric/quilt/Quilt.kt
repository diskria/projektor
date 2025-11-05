package io.github.diskria.projektor.minecraft.loaders.fabric.quilt

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

data object Quilt : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
            val configPath = fileName(getLoaderName(), "mod", Constants.File.Extension.JSON)
        }
    }
}