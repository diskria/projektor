package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.extensions.forge
import io.github.diskria.projektor.extensions.minecraft
import io.github.diskria.projektor.minecraft.loaders.common.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

object Forge : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
//            forge {
//                mappings("parchment", "2025.10.12-" + mod.minecraftVersion.asString())
//            }
//            dependencies {
//                minecraft("net.minecraftforge", "forge", "${mod.minecraftVersion.asString()}-${"60.0.17"}")
//            }
        }
    }
}
