package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

sealed interface ModLoader {
    fun getConfigFilePath(): String
    fun configure(project: Project, mod: MinecraftMod): Any
    fun getName(): String = mapToEnum().getName()
}
