package io.github.diskria.projektor.minecraft.loaders.forge.neoforge.config

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeModConfig(
    val license: String,

    @SerialName("issueTrackerURL")
    val issuesUrl: String,

    val mods: List<NeoforgeConfigModEntry>,
    val mixins: List<NeoforgeConfigMixinEntry>,
    val accessTransformers: List<NeoforgeConfigAccessTransformerEntry>,
    val dependencies: Map<String, NeoforgeConfigDependencyEntry>,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeModConfig =
            NeoforgeModConfig(
                license = mod.license.id,
                issuesUrl = mod.repo.getIssuesUrl(),
                mods = listOf(
                    NeoforgeConfigModEntry.of(mod),
                ),
                mixins = listOf(
                    NeoforgeConfigMixinEntry.of(mod),
                ),
                accessTransformers = listOf(
                    NeoforgeConfigAccessTransformerEntry.of(mod),
                ),
                dependencies = mapOf(
                    NeoforgeConfigDependencyEntry.createMinecraftDependency(mod),
                    NeoforgeConfigDependencyEntry.createLoaderDependency(mod),
                ),
            )
    }
}
