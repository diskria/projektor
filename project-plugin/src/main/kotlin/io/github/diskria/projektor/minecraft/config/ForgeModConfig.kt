package io.github.diskria.projektor.minecraft.config

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.projektor.minecraft.config.versions.range.InequalityVersionRange
import io.github.diskria.projektor.minecraft.config.versions.range.VersionRange
import io.github.diskria.projektor.minecraft.loaders.Forge
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ForgeModConfig private constructor(
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
    class ForgeMod private constructor(
        val description: String,
        val version: String,
        val authors: String,
        val dependencies: List<ModDependency>,

        @SerialName("modId")
        val id: String,

        @SerialName("displayName")
        val name: String,

        @SerialName("logoFile")
        val icon: String,

        @SerialName("displayURL")
        val modrinthProjectUrl: String,
    ) {
        companion object {
            fun of(
                mod: MinecraftMod,
                environment: ModEnvironment,
                minSupportedVersion: String,
                forgeVersion: String,
                modrinthProjectUrl: String,
            ): ForgeMod =
                ForgeMod(
                    description = mod.description,
                    version = mod.version,
                    authors = mod.repo.owner.developer,
                    dependencies = listOf(
                        MinecraftDependency(
                            environment,
                            InequalityVersionRange.min(VersionBound.inclusive(minSupportedVersion)),
                        ),
                        ForgeDependency(
                            environment,
                            InequalityVersionRange.min(VersionBound.inclusive(forgeVersion))
                        ),
                    ),
                    id = mod.id,
                    name = mod.id,
                    icon = fileName("icon", Constants.File.Extension.PNG),
                    modrinthProjectUrl = modrinthProjectUrl,
                )
        }
    }

    @Serializable
    open class ModDependency(
        val versionRange: String,
        val ordering: String,
        val side: String,

        @SerialName("modId")
        val id: String,

        @SerialName("mandatory")
        val isMandatory: Boolean,
    )

    open class InternalModDependency(
        id: String,
        environment: ModEnvironment,
        versionRange: String
    ) : ModDependency(
        versionRange,
        "NONE",
        environment.forgeConfigValue,
        id,
        true,
    )

    class MinecraftDependency(
        environment: ModEnvironment,
        versionRange: String
    ) : InternalModDependency(
        "minecraft",
        environment,
        versionRange
    )

    class ForgeDependency(
        environment: ModEnvironment,
        versionRange: String
    ) : InternalModDependency(
        Forge.getName(),
        environment,
        versionRange
    )

    companion object {
        fun of(
            mod: MinecraftMod,
            environment: ModEnvironment,
            minSupportedVersion: String,
            forgeVersion: String,
            loaderVersion: String,
            modrinthProjectUrl: String,
            versionRange: VersionRange,
        ): ForgeModConfig {
            val mods = listOf(
                ForgeMod.of(mod, environment, minSupportedVersion, forgeVersion, modrinthProjectUrl),
            )
            return ForgeModConfig(
                loaderVersion = versionRange.min(VersionBound.inclusive(loaderVersion)),
                license = mod.license.id,
                mods = mods,
                dependencies = mods.associate { it.id to it.dependencies },
                loader = "javafml",
                issueTrackerUrl = mod.repo.getIssuesUrl(),
                isClientSideOnly = environment == ModEnvironment.CLIENT_SIDE_ONLY,
            )
        }
    }
}
