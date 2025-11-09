package io.github.diskria.projektor.minecraft.configs.forge

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForgeModAccessTransformerConfigEntry(
    @SerialName("file")
    val accessTransformerPath: String,
) {
    companion object {
        fun of(mod: MinecraftMod): ForgeModAccessTransformerConfigEntry =
            ForgeModAccessTransformerConfigEntry(
                accessTransformerPath = mod.accessorConfigPath
            )
    }
}
