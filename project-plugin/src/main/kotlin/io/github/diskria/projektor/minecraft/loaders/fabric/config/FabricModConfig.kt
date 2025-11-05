package io.github.diskria.projektor.minecraft.loaders.fabric.config

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.getFabricApiDependencyName
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.versions.VersionBound
import io.github.diskria.projektor.minecraft.versions.range.InequalityVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
@EncodeDefaults
@PrettyPrint
class FabricModConfig private constructor(
    val schemaVersion: Int = 1,
    val id: String,
    val version: String,
    val name: String,
    val description: String,
    val authors: List<String>,
    val license: String,
    val icon: String,
    val environment: String,
    val mixins: List<String>,
    val accessWidener: String,

    @SerialName("contact")
    val links: Links,

    @SerialName("entrypoints")
    val entryPoints: EntryPoints,

    @SerialName("depends")
    val dependencies: Map<String, String>,
) {
    @Serializable
    class Links private constructor(
        @SerialName("homepage")
        val homepageUrl: String,

        @SerialName("sources")
        val sourceCodeUrl: String,

        @SerialName("issues")
        val issuesUrl: String,
    ) {
        companion object {
            fun of(homepageUrl: Url, sourceCodeUrl: String, issuesUrl: String): Links =
                Links(homepageUrl.toString(), sourceCodeUrl, issuesUrl)
        }
    }

    @Serializable
    class EntryPoints private constructor(
        @SerialName("main")
        val mainEntryPoints: List<String>? = null,

        @SerialName("client")
        val clientEntryPoints: List<String>? = null,

        @SerialName("server")
        val serverEntryPoints: List<String>? = null,
    ) {
        companion object {
            fun of(mod: MinecraftMod): EntryPoints {
                val clientPackageName = mod.packageName.appendPackageName(ModSide.CLIENT.getName())
                val serverPackageName = mod.packageName.appendPackageName(ModSide.SERVER.getName())
                val clientEntryPoint = clientPackageName.appendPackageName(mod.getEntryPointName(ModSide.CLIENT))
                val serverEntryPoint = serverPackageName.appendPackageName(mod.getEntryPointName(ModSide.SERVER))
                return when (mod.config.environment) {
                    ModEnvironment.CLIENT_SERVER -> EntryPoints(
                        mainEntryPoints = listOf(serverEntryPoint),
                        clientEntryPoints = listOf(clientEntryPoint),
                    )

                    ModEnvironment.CLIENT -> EntryPoints(
                        clientEntryPoints = listOf(clientEntryPoint),
                    )

                    ModEnvironment.DEDICATED_SERVER -> EntryPoints(
                        serverEntryPoints = listOf(serverEntryPoint),
                    )
                }
            }
        }
    }

    companion object {
        fun of(mod: MinecraftMod): FabricModConfig =
            FabricModConfig(
                id = mod.id,
                version = mod.version,
                name = mod.name,
                description = mod.description,
                authors = listOf(mod.repo.owner.developer),
                license = mod.license.id,
                icon = buildPath("assets", mod.id, fileName("icon", Constants.File.Extension.PNG)),
                environment = mod.getEnvironmentConfigValue(),
                accessWidener = buildPath("assets", mod.id, mod.getAccessWidenerFileName()),
                mixins = listOf(buildPath("assets", mod.id, mod.mixinsConfigFileName)),
                links = Links.of(
                    homepageUrl = mod.metadata.publishingTargets
                        .first { it == PublishingTargetType.MODRINTH }
                        .mapToModel()
                        .getHomepage(mod.metadata),
                    sourceCodeUrl = mod.repo.getUrl(),
                    issuesUrl = mod.repo.getIssuesUrl(),
                ),
                entryPoints = EntryPoints.of(mod),
                dependencies = listOfNotNull(
                    "java" to InequalityVersionRange.min(
                        VersionBound.inclusive(
                            mod.jvmTarget.toInt().toString()
                        )
                    ),
                    "minecraft" to InequalityVersionRange.min(
                        VersionBound.inclusive(
                            mod.minSupportedVersion.asString()
                        )
                    ),
                    "fabricloader" to InequalityVersionRange.min(
                        VersionBound.inclusive(
                            mod.config.fabric.loader
                        )
                    ),
                    mod.minSupportedVersion.getFabricApiDependencyName() to InequalityVersionRange.any,
                ).toMap()
            )
    }
}