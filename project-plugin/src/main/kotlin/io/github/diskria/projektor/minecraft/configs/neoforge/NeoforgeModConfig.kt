package io.github.diskria.projektor.minecraft.configs.neoforge

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeModConfig(
    @SerialName("license")
    val licenseId: String,

    @SerialName("issueTrackerURL")
    val issuesUrl: String,

    val mods: List<NeoforgeModConfigEntry>,
    val mixins: List<NeoforgeModMixinConfigEntry>,
    val accessTransformers: List<NeoforgeModAccessTransformerConfigEntry>,
    val dependencies: Map<String, List<NeoforgeModDependencyConfigEntry>>,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeModConfig =
            NeoforgeModConfig(
                licenseId = mod.license.id,
                issuesUrl = mod.repo.getIssuesUrl(),
                mods = listOf(
                    NeoforgeModConfigEntry.of(mod),
                ),
                mixins = listOf(
                    NeoforgeModMixinConfigEntry.of(mod),
                ),
                accessTransformers = listOf(
                    NeoforgeModAccessTransformerConfigEntry.of(mod),
                ),
                dependencies = mapOf(
                    mod.id to listOf(
                        NeoforgeModDependencyConfigEntry.createMinecraftDependency(mod),
                        NeoforgeModDependencyConfigEntry.createLoaderDependency(mod),
                    )
                ),
            )
    }
}
