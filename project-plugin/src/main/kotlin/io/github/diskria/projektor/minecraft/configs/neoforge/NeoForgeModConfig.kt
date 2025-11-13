package io.github.diskria.projektor.minecraft.configs.neoforge

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoForgeModConfig(
    @SerialName("license")
    val licenseId: String,

    @SerialName("issueTrackerURL")
    val issuesUrl: String,

    val mods: List<NeoForgeModConfigEntry>,
    val mixins: List<NeoForgeModMixinConfigEntry>,
    val accessTransformers: List<NeoForgeModAccessTransformerConfigEntry>,
    val dependencies: Map<String, List<NeoForgeModDependencyConfigEntry>>,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoForgeModConfig =
            NeoForgeModConfig(
                licenseId = mod.license.id,
                issuesUrl = mod.repo.getIssuesUrl(),
                mods = listOf(
                    NeoForgeModConfigEntry.of(mod),
                ),
                mixins = listOf(
                    NeoForgeModMixinConfigEntry.of(mod),
                ),
                accessTransformers = listOf(
                    NeoForgeModAccessTransformerConfigEntry.of(mod),
                ),
                dependencies = mapOf(
                    mod.id to listOf(
                        NeoForgeModDependencyConfigEntry.createMinecraftDependency(mod),
                        NeoForgeModDependencyConfigEntry.createLoaderDependency(mod),
                    )
                ),
            )
    }
}
