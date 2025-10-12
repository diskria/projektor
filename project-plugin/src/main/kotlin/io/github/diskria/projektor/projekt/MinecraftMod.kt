package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.minecraft.ModLoader
import io.github.diskria.projektor.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.minecraft.utils.ModrinthUtils
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getMinJavaVersion
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.projekt.common.IProjekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

open class MinecraftMod(
    projekt: IProjekt,
    val config: MinecraftModConfiguration,
    val modLoader: ModLoader,
    val minecraftVersion: MinecraftVersion,
) : IProjekt by projekt {

    val id: String = repo
    val mixinsConfigFileName: String = fileName(id, "mixins", Constants.File.Extension.JSON)
    val modrinthProjectUrl: String = ModrinthUtils.getProjectUrl(config.modrinthProjectId)

    override val jvmTarget: JvmTarget
        get() = minecraftVersion.getMinJavaVersion().toJvmTarget()

    override val jarVersion: String
        get() = buildString {
            append(modLoader.getName())
            append(Constants.Char.HYPHEN)
            append(version)
            append(Constants.Char.PLUS)
            append(SHORT_MINECRAFT_NAME)
            append(minecraftVersion.getVersion())
        }

    override fun getBuildConfigFields(): List<Property<String>> {
        val modId by id.autoNamedProperty(SCREAMING_SNAKE_CASE)
        val modName by name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(modId, modName)
    }

    companion object {
        private const val SHORT_MINECRAFT_NAME: String = "mc"
    }
}
