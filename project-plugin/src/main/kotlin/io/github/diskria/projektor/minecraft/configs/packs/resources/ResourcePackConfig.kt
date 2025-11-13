package io.github.diskria.projektor.minecraft.configs.packs.resources

import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.minecraft.configs.packs.common.PackConfig
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.Serializable

@Serializable
@PrettyPrint
data class ResourcePackConfig(
    val pack: PackConfig,
) {
    companion object {
        fun of(mod: MinecraftMod, format: String): ResourcePackConfig =
            ResourcePackConfig(
                pack = PackConfig.of(mod, format)
            )
    }
}
