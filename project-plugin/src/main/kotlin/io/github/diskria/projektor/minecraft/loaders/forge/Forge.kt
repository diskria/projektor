package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.projektor.extensions.forge
import io.github.diskria.projektor.minecraft.loaders.common.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

data object Forge : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
//            forge {
//
//            }
        }
    }
}
