package io.github.diskria.projektor.minecraft.configs.forge

import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.minecraft.versions.VersionBound
import io.github.diskria.projektor.minecraft.versions.range.IntervalVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForgeModConfig(
    val modLoader: String,

    val loaderVersion: String,

    @SerialName("license")
    val licenseId: String,

    @SerialName("issueTrackerURL")
    val issuesUrl: String,

    @SerialName("clientSideOnly")
    val isClientSideOnly: Boolean,

    val mods: List<ForgeModConfigEntry>,
    val dependencies: Map<String, List<ForgeModDependencyConfigEntry>>,
) {
    companion object {
        fun of(mod: MinecraftMod): ForgeModConfig =
            ForgeModConfig(
                modLoader = "javafml",
                loaderVersion = IntervalVersionRange.min(VersionBound.inclusive(mod.config.forge.loader)),
                licenseId = mod.license.id,
                issuesUrl = mod.repo.getIssuesUrl(),
                isClientSideOnly = mod.config.environment == ModEnvironment.CLIENT,
                mods = listOf(
                    ForgeModConfigEntry.of(mod),
                ),
                dependencies = mapOf(
                    mod.id to listOf(
                        ForgeModDependencyConfigEntry.createMinecraftDependency(mod),
                        ForgeModDependencyConfigEntry.createLoaderDependency(mod),
                    )
                ),
            )
    }
}
