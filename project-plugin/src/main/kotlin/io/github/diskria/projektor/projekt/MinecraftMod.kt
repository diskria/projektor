package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.getMinJavaVersion
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class MinecraftMod(
    projekt: Projekt,
    val config: MinecraftModConfiguration,
    val loader: ModLoader,
    val supportedVersionRange: MinecraftVersionRange,
) : AbstractProjekt(projekt) {

    val id: String = repo.name
    val mixinsConfigFileName: String = fileName(id, "mixins", Constants.File.Extension.JSON)

    override val isJavadocEnabled: Boolean = false

    override val jvmTarget: JvmTarget
        get() {
            val start = supportedVersionRange.min.getMinJavaVersion().toJvmTarget()
            val end = supportedVersionRange.max.getMinJavaVersion().toJvmTarget()
            if (start != end) {
                gradleError("Minecraft version range crosses Java compatibility boundary: $start -> $end")
            }
            return end
        }

    override val archiveVersion: String
        get() = buildString {
            append(loader.getName())
            append(Constants.Char.HYPHEN)
            append(version)
            append(Constants.Char.PLUS)
            append(SHORT_NAME)
            append(supportedVersionRange.max.asString())
        }

    override fun getBuildConfigFields(): List<Property<String>> {
        val modId by id.autoNamedProperty(SCREAMING_SNAKE_CASE)
        val modName by name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(modId, modName)
    }

    companion object {
        const val SHORT_NAME: String = "mc"
        const val RUN_DIRECTORY_NAME: String = "minecraft-run"
    }
}
