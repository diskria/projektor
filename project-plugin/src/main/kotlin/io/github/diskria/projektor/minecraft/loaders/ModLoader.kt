package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

abstract class ModLoader {

    val family: ModLoaderFamily
        get() = when (mapToEnum()) {
            ModLoaderType.FABRIC, ModLoaderType.LEGACY_FABRIC, ModLoaderType.ORNITHE -> ModLoaderFamily.FABRIC
            ModLoaderType.FORGE, ModLoaderType.NEOFORGE -> ModLoaderFamily.FORGE
        }

    abstract fun configure(modProject: Project, sideProjects: Map<ModSide, Project>, mod: MinecraftMod): Any

    fun getLoaderName(): String = mapToEnum().getName()

    fun getLoaderDisplayName(): String = this::class.className()
}
