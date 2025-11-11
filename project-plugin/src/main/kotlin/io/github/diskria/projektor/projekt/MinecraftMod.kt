package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.snake_case
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.minecraft.MinecraftConstants
import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.*
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt
import io.ktor.http.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class MinecraftMod(
    projekt: Projekt,
    val config: MinecraftModConfiguration,
    val loader: ModLoader,
    val supportedVersionRange: MinecraftVersionRange,
) : AbstractProjekt(projekt) {

    val id: String = repo.name.setCase(`kebab-case`, snake_case)
    val minSupportedVersion: MinecraftVersion = supportedVersionRange.min
    val maxSupportedVersion: MinecraftVersion = supportedVersionRange.max
    val minecraftVersion: MinecraftVersion = minSupportedVersion

    val assetsPath: String =
        "assets".appendPath(id)

    val iconFileName: String =
        fileName("icon", Constants.File.Extension.PNG)

    val iconPath: String =
        assetsPath.appendPath(iconFileName)

    val accessorConfigFileName: String =
        when (loader.family) {
            ModLoaderFamily.FABRIC -> fileName(id, "accesswidener")
            ModLoaderFamily.FORGE -> fileName("accesstransformer", "cfg")
        }

    val accessorConfigPath: String =
        assetsPath.appendPath(accessorConfigFileName)

    val mixinsConfigFileName: String =
        fileName(id, "mixins", Constants.File.Extension.JSON)

    val mixinsConfigPath: String =
        assetsPath.appendPath(mixinsConfigFileName)

    val resourcePackConfigFileName: String =
        fileName("pack", "mcmeta")

    val configFileName: String =
        when (val type = loader.mapToEnum()) {
            ModLoaderType.FABRIC, ModLoaderType.LEGACY_FABRIC, ModLoaderType.ORNITHE -> {
                fileName(ModLoaderType.FABRIC.getName(), "mod", Constants.File.Extension.JSON)
            }

            else -> {
                if (type == ModLoaderType.NEOFORGE && minecraftVersion >= Release.V_1_20_5) {
                    fileName(type.getName(), "mods", Constants.File.Extension.TOML)
                } else {
                    fileName("mods", Constants.File.Extension.TOML)
                }
            }
        }

    val configFileParentPath: String =
        when (loader.family) {
            ModLoaderFamily.FABRIC -> Constants.Char.EMPTY
            ModLoaderFamily.FORGE -> "META-INF"
        }

    val configEnvironment: String
        get() {
            val singleSide = config.environment.sides.singleOrNull()
            return when (loader.family) {
                ModLoaderFamily.FABRIC -> singleSide?.getName() ?: Constants.Char.ASTERISK.toString()
                ModLoaderFamily.FORGE -> singleSide?.getName(SCREAMING_SNAKE_CASE) ?: "BOTH"
            }
        }

    val developerPlayerName: String =
        repo.owner.developer + MinecraftConstants.PLAYER_NAME_DEVELOPER_SUFFIX

    override val isJavadocEnabled: Boolean = false

    override val javaVersion: Int
        get() = config.javaVersion ?: super.javaVersion

    override val jvmTarget: JvmTarget
        get() {
            val minJvmTarget = minSupportedVersion.minJavaVersion.toJvmTarget()
            val maxJvmTarget = maxSupportedVersion.minJavaVersion.toJvmTarget()
            if (minJvmTarget != maxJvmTarget) {
                gradleError("Minecraft version range crosses Java compatibility: $minJvmTarget -> $maxJvmTarget")
            }
            return maxJvmTarget
        }

    override val archiveVersion: String
        get() = buildString {
            append(loader.getLoaderName())
            append(Constants.Char.HYPHEN)
            append(version)
            append(Constants.Char.PLUS)
            append(MinecraftConstants.SHORT_GAME_NAME)
            if (minSupportedVersion != maxSupportedVersion) {
                append(minSupportedVersion.asString())
                append(Constants.Char.HYPHEN)
            }
            append(maxSupportedVersion.asString())
        }

    fun getEntryPointName(side: ModSide): String =
        buildString {
            append(MinecraftConstants.FULL_GAME_NAME)
            if (side == ModSide.CLIENT || config.environment == ModEnvironment.DEDICATED_SERVER) {
                append(side.getName(PascalCase))
            }
            append("Mod")
        }

    fun getModrinthUrl(): Url =
        metadata.publishingTargets
            .first { it == PublishingTargetType.MODRINTH }
            .mapToModel()
            .getHomepage(metadata)

    override fun getBuildConfigFields(): List<Property<String>> {
        val modId by id.autoNamedProperty(SCREAMING_SNAKE_CASE)
        val modName by name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(modId, modName)
    }
}
