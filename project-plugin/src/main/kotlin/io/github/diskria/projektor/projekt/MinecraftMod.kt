package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.getMinJavaVersion
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.helpers.AccessWidenerHelper
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.ModEnvironment.CLIENT_SERVER
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.fabric.ornithe.Ornithe
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
    val minSupportedVersion: MinecraftVersion = supportedVersionRange.min
    val maxSupportedVersion: MinecraftVersion = supportedVersionRange.max

    override val isJavadocEnabled: Boolean = false

    override val jvmTarget: JvmTarget
        get() {
            val minJvmTarget = minSupportedVersion.getMinJavaVersion().toJvmTarget()
            val maxJvmTarget = maxSupportedVersion.getMinJavaVersion().toJvmTarget()
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
            append(SHORT_NAME)
            append(minSupportedVersion.asString())
            append(Constants.Char.HYPHEN)
            append(maxSupportedVersion.asString())
        }

    fun getEnvironmentConfigValue(): String {
        val isFabricFamily = loader == Fabric || loader == Ornithe
        return when (val environment = config.environment) {
            CLIENT_SERVER -> if (isFabricFamily) Constants.Char.ASTERISK.toString() else "BOTH"
            else -> environment.sides.single().getName().modifyUnless(isFabricFamily) { it.uppercase() }
        }
    }

    fun getEntryPointName(side: ModSide): String =
        buildString {
            append("Minecraft")
            if (side == ModSide.CLIENT) {
                append("Client")
            } else if (config.environment == ModEnvironment.DEDICATED_SERVER_ONLY) {
                append("Server")
            }
            append("Mod")
        }

    fun getAccessWidenerFileName(): String =
        AccessWidenerHelper.getFileName(id)

    fun getAccessTransformerFileName(): String =
        "accesstransformer.cfg"

    override fun getBuildConfigFields(): List<Property<String>> {
        val modId by id.autoNamedProperty(SCREAMING_SNAKE_CASE)
        val modName by name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(modId, modName)
    }

    companion object {
        const val SHORT_NAME: String = "mc"
        const val RUN_DIRECTORY_NAME: String = "run"
    }
}
