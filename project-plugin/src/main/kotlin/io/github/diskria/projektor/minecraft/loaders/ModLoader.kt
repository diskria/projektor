package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

abstract class ModLoader {

    abstract fun configure(modProject: Project, mod: MinecraftMod): Any

    fun getLoaderName(): String = mapToEnum().getName()

    fun getLoaderDisplayName(): String = this::class.className()
}
