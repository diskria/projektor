package io.github.diskria.projektor.minecraft.configs.packs.common

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackConfig(
    val description: String,

    @SerialName("min_format")
    val minFormat: String,

    @SerialName("max_format")
    val maxFormat: String,
) {
    companion object {
        fun of(mod: MinecraftMod, minFormat: String, maxFormat: String): PackConfig =
            PackConfig(
                description = "${mod.name} resources",
                minFormat = minFormat,
                maxFormat = maxFormat,
            )
    }
}