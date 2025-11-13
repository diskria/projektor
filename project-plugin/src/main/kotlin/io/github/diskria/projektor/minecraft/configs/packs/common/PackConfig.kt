package io.github.diskria.projektor.minecraft.configs.packs.common

import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.versions.compareTo
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackConfig(
    @SerialName("pack_format")
    val format: Int? = null,

    val description: String,

    @SerialName("min_format")
    val minFormat: String? = null,
) {
    companion object {
        fun of(mod: MinecraftMod, format: String): PackConfig {
            val description = "${mod.name} resources"
            return when {
                mod.minecraftVersion < Release.V_1_21_9 -> PackConfig(
                    format = format.toInt(),
                    description = description,
                )

                else -> PackConfig(
                    description = description,
                    minFormat = format,
                )
            }
        }
    }
}
