package io.github.diskria.projektor.minecraft.loaders.forge.neoforge.config

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeConfigAccessTransformerEntry(
    val file: String,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeConfigAccessTransformerEntry =
            NeoforgeConfigAccessTransformerEntry(
                file = "META-INF/accesstransformer.cfg"
            )
    }
}
