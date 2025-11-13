package io.github.diskria.projektor.minecraft.configs.neoforge

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoForgeModAccessTransformerConfigEntry(
    @SerialName("file")
    val accessorConfigPath: String,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoForgeModAccessTransformerConfigEntry =
            NeoForgeModAccessTransformerConfigEntry(
                accessorConfigPath = mod.accessorConfigPath
            )
    }
}
