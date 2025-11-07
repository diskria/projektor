package io.github.diskria.projektor.minecraft.loaders.neoforge.config

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeModAccessTransformerConfigEntry(
    @SerialName("file")
    val accessTransformerPath: String,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeModAccessTransformerConfigEntry =
            NeoforgeModAccessTransformerConfigEntry(
                accessTransformerPath = mod.accessorConfigPath
            )
    }
}
