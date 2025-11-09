package io.github.diskria.projektor.minecraft.configs.neoforge

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeModMixinConfigEntry(
    @SerialName("config")
    val mixinsConfigPath: String,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeModMixinConfigEntry =
            NeoforgeModMixinConfigEntry(
                mixinsConfigPath = mod.mixinsConfigPath,
            )
    }
}
