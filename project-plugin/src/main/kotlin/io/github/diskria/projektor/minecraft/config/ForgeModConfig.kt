package io.github.diskria.projektor.minecraft.config

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.addElements
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.projektor.minecraft.config.versions.range.InequalityVersionRange
import io.github.diskria.projektor.minecraft.config.versions.range.VersionRange
import io.github.diskria.projektor.minecraft.loaders.Forge
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ForgeModConfig(
    val loaderVersion: String,
    val license: String,
    val mods: List<ForgeMod>,
    val dependencies: Map<String, List<ModDependency>>,

    @SerialName("modLoader")
    val loader: String,

    @SerialName("issueTrackerURL")
    val issueTrackerUrl: String,

    @SerialName("clientSideOnly")
    val isClientSideOnly: Boolean,
) {
    @Serializable
    data class ForgeMod(
        val description: String,
        val version: String,
        val authors: String,

        @SerialName("modId")
        val id: String,

        @SerialName("displayName")
        val name: String,

        @SerialName("logoFile")
        val icon: String,

        @SerialName("displayURL")
        val modrinthProjectUrl: String,
    ) {
        val dependencies: MutableList<ModDependency> = mutableListOf()

        companion object {
            fun of(
                mod: MinecraftMod,
                environment: ModEnvironment,
                minecraftVersion: String,
                forgeVersion: String,
                modrinthProjectUrl: String,
            ): ForgeMod =
                ForgeMod(
                    description = mod.description,
                    version = mod.version,
                    authors = mod.repository.owner.developerName,
                    id = mod.id,
                    name = mod.id,
                    icon = fileName("icon", Constants.File.Extension.PNG),
                    modrinthProjectUrl = modrinthProjectUrl,
                ).apply {
                    dependencies.addElements(
                        MinecraftDependency(
                            environment,
                            InequalityVersionRange.min(VersionBound.inclusive(minecraftVersion)),
                        ),
                        ForgeDependency(
                            environment,
                            InequalityVersionRange.min(VersionBound.inclusive(forgeVersion))
                        ),
                    )
                }
        }
    }

    @Serializable
    open class ModDependency(
        @SerialName("modId")
        val id: String,

        @SerialName("mandatory")
        val isMandatory: Boolean,

        val versionRange: String,
        val ordering: String,
        val side: String,
    )

    open class InternalModDependency(
        id: String,
        environment: ModEnvironment,
        versionRange: String
    ) : ModDependency(id, true, versionRange, "NONE", environment.forgeConfigValue)

    class MinecraftDependency(
        environment: ModEnvironment,
        versionRange: String,
    ) : InternalModDependency("minecraft", environment, versionRange)

    class ForgeDependency(
        environment: ModEnvironment,
        versionRange: String,
    ) : InternalModDependency(Forge.getName(), environment, versionRange)

    companion object {
        fun of(
            mod: MinecraftMod,
            environment: ModEnvironment,
            minecraftVersion: String,
            forgeVersion: String,
            loaderVersion: String,
            modrinthProjectUrl: String,
            versionRange: VersionRange,
        ): ForgeModConfig {
            val mods = listOf(
                ForgeMod.of(mod, environment, minecraftVersion, forgeVersion, modrinthProjectUrl),
            )
            return ForgeModConfig(
                loaderVersion = versionRange.min(VersionBound.inclusive(loaderVersion)),
                license = mod.license.id,
                mods = mods,
                dependencies = mods.associate { it.id to it.dependencies },
                loader = "javafml",
                issueTrackerUrl = mod.repository.getIssuesUrl(),
                isClientSideOnly = environment == ModEnvironment.CLIENT_SIDE_ONLY,
            )
        }
    }
}
