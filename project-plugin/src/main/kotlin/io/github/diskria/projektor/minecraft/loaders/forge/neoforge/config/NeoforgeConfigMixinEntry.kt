package io.github.diskria.projektor.minecraft.loaders.forge.neoforge.config

import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeConfigMixinEntry(
    val config: String,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeConfigMixinEntry =
            NeoforgeConfigMixinEntry(
                config = buildPath("assets", mod.id, mod.mixinsConfigFileName),
            )
    }
}
