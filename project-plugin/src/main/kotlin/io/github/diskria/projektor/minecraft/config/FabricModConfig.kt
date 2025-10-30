package io.github.diskria.projektor.minecraft.config

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.common.minecraft.era.fabric.FabricEra
import io.github.diskria.projektor.common.minecraft.era.fabric.OrnitheFabricEra
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.getFabricApiDependencyName
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.projektor.minecraft.config.versions.range.InequalityVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        val modrinthProjectUrl: String,

        @SerialName("sources")
        val sourceCodeUrl: String,

        @SerialName("issues")
        val issueTrackerUrl: String,
    ) {
        companion object {
            fun of(
                modrinthProjectUrl: String,
                sourceCodeUrl: String,
                issueTrackerUrl: String,
            ): Links =
                Links(
                    modrinthProjectUrl,
                    sourceCodeUrl,
                    issueTrackerUrl,
                )
        }
    }

    @Serializable
    class EntryPoints private constructor(
        @SerialName("main")
        val mainEntryPoints: List<EntryPoint>? = null,

        @SerialName("client")
        val clientEntryPoints: List<EntryPoint>? = null,

        @SerialName("fabric-datagen")
        val dataGeneratorEntryPoints: List<EntryPoint>? = null,
    ) {
        companion object {
            fun of(mod: MinecraftMod, dataGenerators: List<String>): EntryPoints {
                val classPathPrefix = mod.packageName.appendPackageName(mod.classNamePrefix)
                val mainEntryPoints = entryPoints(mod.minSupportedVersion, classPathPrefix + "Mod")
                val clientEntryPoints = entryPoints(mod.minSupportedVersion, classPathPrefix + "Client")
                val dataGeneratorEntryPoints =
                    entryPoints(mod.minSupportedVersion, *dataGenerators.toTypedArray()).toNullIfEmpty()

                return when (mod.config.environment) {
                    ModEnvironment.CLIENT_SERVER -> EntryPoints(
                        mainEntryPoints = mainEntryPoints,
                        clientEntryPoints = clientEntryPoints,
                        dataGeneratorEntryPoints = dataGeneratorEntryPoints,
                    )

                    ModEnvironment.CLIENT_SIDE_ONLY -> EntryPoints(
                        clientEntryPoints = clientEntryPoints,
                        dataGeneratorEntryPoints = dataGeneratorEntryPoints,
                    )

                    ModEnvironment.SERVER_SIDE_ONLY -> EntryPoints(
                    )
                }
            }

            private fun entryPoints(minecraftVersion: MinecraftVersion, vararg classPaths: String): List<EntryPoint> =
                classPaths.map { EntryPoint.of(minecraftVersion, it) }
        }
    }

    @Serializable
    class EntryPoint private constructor(
        @SerialName("adapter")
        val language: String? = null,

        @SerialName("value")
        val classPath: String,
    ) {
        companion object {
            fun of(minecraftVersion: MinecraftVersion, classPath: String): EntryPoint =
                EntryPoint(
                    language = if (FabricEra.includesVersion(minecraftVersion)) "kotlin" else null,
                    classPath = classPath,
                )
        }
    }

    companion object {
        fun of(
            mod: MinecraftMod,
            minSupportedVersion: MinecraftVersion,
            loaderVersion: String,
            isApiRequired: Boolean,
            dataGenerators: List<String>,
        ): FabricModConfig =
            FabricModConfig(
                id = mod.id,
                version = mod.version,
                name = mod.name,
                description = mod.description,
                authors = listOf(mod.repo.owner.developer),
                license = mod.license.id,
                icon = buildPath("assets", mod.id, fileName("icon", Constants.File.Extension.PNG)),
                environment = mod.config.environment.fabricConfigValue,
                accessWidener = fileName(mod.id, "accesswidener"),
                mixins = listOf(mod.mixinsConfigFileName),
                links = Links.of(
                    modrinthProjectUrl = mod.metadata.publishingTargets
                        .first { it == PublishingTargetType.MODRINTH }
                        .mapToModel()
                        .getHomepage(mod.metadata),
                    sourceCodeUrl = mod.repo.getUrl(),
                    issueTrackerUrl = mod.repo.getIssuesUrl(),
                ),
                entryPoints = EntryPoints.of(mod, dataGenerators),
                dependencies = listOfNotNull(
                    "java" to InequalityVersionRange.min(VersionBound.inclusive(mod.jvmTarget.toInt().toString())),
                    "minecraft" to InequalityVersionRange.min(VersionBound.inclusive(minSupportedVersion.asString())),
                    "fabricloader" to InequalityVersionRange.min(VersionBound.inclusive(loaderVersion)),
                    when {
                        OrnitheFabricEra.includesVersion(minSupportedVersion) -> "osl-entrypoints" to InequalityVersionRange.min(VersionBound.inclusive("0.4.0"))
                        else -> null
                    },
                    when {
                        FabricEra.includesVersion(minSupportedVersion) -> "fabric-language-kotlin" to InequalityVersionRange.any
                        else -> null
                    },
                    when {
                        isApiRequired -> minSupportedVersion.getFabricApiDependencyName() to InequalityVersionRange.any
                        else -> null
                    },
                ).toMap()
            )
    }
}
