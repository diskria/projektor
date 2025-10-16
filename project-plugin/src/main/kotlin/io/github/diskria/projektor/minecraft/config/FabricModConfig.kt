package io.github.diskria.projektor.minecraft.config

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.projektor.minecraft.config.versions.range.InequalityVersionRange
import io.github.diskria.projektor.minecraft.config.versions.range.VersionRange
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@PrettyPrint
class FabricModConfig(
    val schemaVersion: Int,
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
    val dependencies: Dependencies,
) {
    @Serializable
    class Links private constructor(
        @SerialName("homepage")
        val modrinthProjectUrl: String,

        @SerialName("sources")
        val sourceCodeUrl: String,
    ) {
        companion object {
            fun of(modrinthProjectUrl: String, sourceCodeUrl: String): Links =
                Links(
                    modrinthProjectUrl = modrinthProjectUrl,
                    sourceCodeUrl = sourceCodeUrl,
                )
        }
    }

    @Serializable
    class EntryPoints(
        @SerialName("main")
        val mainEntryPoints: List<EntryPoint>? = null,

        @SerialName("client")
        val clientEntryPoints: List<EntryPoint>? = null,

        @SerialName("server")
        val serverEntryPoints: List<EntryPoint>? = null,

        @SerialName("fabric-datagen")
        val datagenEntryPoints: List<EntryPoint>? = null,
    ) {
        companion object {
            fun of(mod: MinecraftMod, datagenClasses: List<String>): EntryPoints {
                val classPathPrefix = mod.packageName.appendPackageName(mod.classNamePrefix)
                val mainEntryPoints = entryPoints(classPathPrefix + "Mod")
                val clientEntryPoints = entryPoints(classPathPrefix + "Client")
                val serverEntryPoints = entryPoints(classPathPrefix + "Server")
                val datagenEntryPoints = datagenClasses.map { EntryPoint.of(it) }.toNullIfEmpty()

                return when (mod.config.environment) {
                    ModEnvironment.CLIENT_SERVER -> EntryPoints(
                        mainEntryPoints = mainEntryPoints,
                        clientEntryPoints = clientEntryPoints,
                        serverEntryPoints = serverEntryPoints,
                        datagenEntryPoints = datagenEntryPoints,
                    )

                    ModEnvironment.CLIENT_SIDE_ONLY -> EntryPoints(
                        clientEntryPoints = clientEntryPoints,
                        datagenEntryPoints = datagenEntryPoints,
                    )

                    ModEnvironment.SERVER_SIDE_ONLY -> EntryPoints(
                        serverEntryPoints = serverEntryPoints,
                    )
                }
            }

            private fun entryPoints(vararg classPaths: String): List<EntryPoint> =
                classPaths.map { EntryPoint.of(it) }
        }
    }

    @Serializable
    class EntryPoint(
        @SerialName("adapter")
        val language: String,

        @SerialName("value")
        val classPath: String,
    ) {
        companion object {
            fun of(classPath: String): EntryPoint =
                EntryPoint(
                    classPath = classPath,
                    language = "kotlin",
                )
        }
    }

    @Serializable
    class Dependencies(
        @SerialName("java")
        val jvmDependency: String,

        @SerialName("minecraft")
        val minecraftDependency: String,

        @SerialName("fabricloader")
        val loaderDependency: String,

        @SerialName("fabric-language-kotlin")
        val kotlinDependency: String,

        @SerialName("fabric-api")
        val apiDependency: String? = null,
    ) {
        companion object {
            fun of(
                javaVersion: Int,
                minecraftVersion: MinecraftVersion,
                loaderVersion: String,
                isApiRequired: Boolean,
                versionRange: VersionRange = InequalityVersionRange,
            ): Dependencies =
                Dependencies(
                    jvmDependency = versionRange.min(VersionBound.inclusive(javaVersion.toString())),
                    minecraftDependency = versionRange.min(VersionBound.inclusive(minecraftVersion.getVersion())),
                    loaderDependency = versionRange.min(VersionBound.inclusive(loaderVersion)),
                    kotlinDependency = versionRange.any,
                    apiDependency = if (isApiRequired) versionRange.any else null,
                )
        }
    }

    companion object {
        fun of(
            mod: MinecraftMod,
            minecraftVersion: MinecraftVersion,
            loaderVersion: String,
            isApiRequired: Boolean,
            datagenClasses: List<String>,
        ): FabricModConfig =
            FabricModConfig(
                schemaVersion = 1,
                id = mod.id,
                version = mod.metadata.version,
                name = mod.metadata.name,
                description = mod.metadata.description,
                authors = listOf(mod.metadata.repository.owner.developerName),
                license = mod.license.id,
                icon = "assets/${mod.id}/${fileName("icon", Constants.File.Extension.PNG)}",
                environment = mod.config.environment.fabricConfigValue,
                accessWidener = fileName(mod.id, "accesswidener"),
                mixins = listOf(mod.mixinsConfigFileName),
                links = Links.of(
                    modrinthProjectUrl = mod.modrinthUrl,
                    sourceCodeUrl = mod.metadata.repository.getUrl(),
                ),
                entryPoints = EntryPoints.of(
                    mod,
                    datagenClasses,
                ),
                dependencies = Dependencies.of(
                    mod.jvmTarget.toInt(),
                    minecraftVersion,
                    loaderVersion,
                    isApiRequired,
                ),
            )
    }
}
