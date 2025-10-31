package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

sealed class ModLoader {

    abstract val supportedVersionRange: MinecraftVersionRange
    abstract val configFilePath: String

    abstract fun configure(modProject: Project, mod: MinecraftMod): Any

    fun getName(): String = mapToEnum().getName()

    fun getDisplayName(): String = this::class.className()
}
