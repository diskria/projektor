package io.github.diskria.projektor.minecraft.loaders.common

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

abstract class ModLoader {

    val family: ModLoaderFamily
        get() = when (mapToEnum()) {
            FABRIC, LEGACY_FABRIC, ORNITHE -> ModLoaderFamily.FABRIC
            FORGE, NEOFORGE -> ModLoaderFamily.FORGE
        }

    abstract fun configure(modProject: Project, mod: MinecraftMod): Any

    fun getLoaderName(): String = mapToEnum().getName()

    fun getLoaderDisplayName(): String = this::class.className()
}
