package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getMinJavaVersion
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class MinecraftMod(
    projekt: Projekt,
    val config: MinecraftModConfiguration,
    val loader: ModLoader,
    val minecraftVersion: MinecraftVersion,
) : AbstractProjekt(projekt) {

    val id: String = repo.name
    val mixinsConfigFileName: String = fileName(id, "mixins", Constants.File.Extension.JSON)

    override val jvmTarget: JvmTarget
        get() = minecraftVersion.getMinJavaVersion().toJvmTarget()

    override val archiveVersion: String
        get() = buildString {
            append(loader.getName())
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
