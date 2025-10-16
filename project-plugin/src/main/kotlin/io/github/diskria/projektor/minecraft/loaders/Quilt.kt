package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

data object Quilt : ModLoader {

    override fun getConfigFilePath(): String =
        fileName(getName(), "mod", Constants.File.Extension.JSON)

    override fun configureMod(project: Project, minecraftMod: MinecraftMod) = with(project) {
        TODO()
    }
}
