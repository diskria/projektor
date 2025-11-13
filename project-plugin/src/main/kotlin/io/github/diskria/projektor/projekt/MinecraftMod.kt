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
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.*
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.minecraft.loaders.AbstractModLoader
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt
import io.ktor.http.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

class MinecraftMod(
    projekt: Projekt,
    val config: MinecraftModConfiguration,
    val loader: AbstractModLoader,
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

    val accessorConfigPath: String
        get() {
            val parentDirectory = if (ModLoaderType.FORGE == loader.mapToEnum()) "META-INF" else assetsPath
            return parentDirectory.appendPath(accessorConfigFileName)
        }

    val mixinsConfigFileName: String =
        fileName(id, "mixins", Constants.File.Extension.JSON)

    val mixinsConfigPath: String =
        assetsPath.appendPath(mixinsConfigFileName)

    val resourcePackConfigFileName: String =
        fileName("pack", "mcmeta")

    val refmapFileName: String =
        fileName(id + "_refmap", Constants.File.Extension.JSON)

    val configFileName: String =
        when (loader.family) {
            ModLoaderFamily.FABRIC -> fileName(ModLoaderFamily.FABRIC.getName(), "mod", Constants.File.Extension.JSON)
            ModLoaderFamily.FORGE -> when {
                loader.mapToEnum() == ModLoaderType.NEOFORGE && minecraftVersion >= Release.V_1_20_5 -> {
                    fileName(ModLoaderType.NEOFORGE.getName(), "mods", Constants.File.Extension.TOML)
                }

                else -> fileName("mods", Constants.File.Extension.TOML)
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

    val developerUsername: String =
        repo.owner.developer + MinecraftConstants.DEVELOPER_USERNAME_SUFFIX

    val developerOfflineUUID: UUID =
        UUID.nameUUIDFromBytes("OfflinePlayer:$developerUsername".toByteArray(Charsets.UTF_8))

    override val isJavadocEnabled: Boolean = false

    override val isSourcesEnabled: Boolean = false

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

    override val archiveName: String
        get() = id

    override val archiveVersion: String
        get() = buildString {
            append(loader.mapToEnum().getName())
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
            if (minecraftVersion.mappingsType != MappingsType.MERGED ||
                side == ModSide.CLIENT ||
                config.environment == ModEnvironment.DEDICATED_SERVER
            ) {
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
