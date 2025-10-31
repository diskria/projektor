package io.github.diskria.projektor.minecraft.config

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.getFabricApiDependencyName
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.helpers.AccessWidenerHelper
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.projektor.minecraft.config.versions.range.InequalityVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
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
            fun of(homepageUrl: String, sourceCodeUrl: String, issuesUrl: String): Links =
                Links(homepageUrl, sourceCodeUrl, issuesUrl)
        }
    }

    @Serializable
    class EntryPoints private constructor(
        @SerialName("main")
        val mainEntryPoints: List<EntryPoint>? = null,

        @SerialName("client")
        val clientEntryPoints: List<EntryPoint>? = null,
    ) {
        companion object {
            fun of(mod: MinecraftMod): EntryPoints {
                val classPathPrefix = mod.packageName.appendPackageName(mod.classNamePrefix)
                val mainEntryPoints = entryPoints(classPathPrefix + "Mod")
                val clientEntryPoints = entryPoints(classPathPrefix + "Client")

                return when (mod.config.environment) {
                    ModEnvironment.CLIENT_SERVER -> EntryPoints(
                        mainEntryPoints = mainEntryPoints,
                        clientEntryPoints = clientEntryPoints,
                    )

                    ModEnvironment.CLIENT_ONLY -> EntryPoints(
                        clientEntryPoints = clientEntryPoints,
                    )

                    ModEnvironment.DEDICATED_SERVER_ONLY -> EntryPoints(
                        mainEntryPoints = mainEntryPoints,
                    )
                }
            }

            private fun entryPoints(vararg classPaths: String): List<EntryPoint> =
                classPaths.map { EntryPoint.of(it) }
        }
    }

    @Serializable
    class EntryPoint private constructor(
        @SerialName("adapter")
        val language: String = "kotlin",

        @SerialName("value")
        val classPath: String,
    ) {
        companion object {
            fun of(classPath: String): EntryPoint =
                EntryPoint(classPath = classPath)
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
                            Versions.FABRIC_LOADER
                        )
                    ),
                    mod.minSupportedVersion.getFabricApiDependencyName() to InequalityVersionRange.any,
                ).toMap()
            )
    }
}
