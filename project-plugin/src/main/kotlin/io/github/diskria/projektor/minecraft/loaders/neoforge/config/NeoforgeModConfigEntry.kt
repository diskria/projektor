package io.github.diskria.projektor.minecraft.loaders.neoforge.config

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeModConfigEntry(
    @SerialName("modId")
    val id: String,

    val version: String,

    @SerialName("displayName")
    val name: String,

    @SerialName("updateJSONURL")
    val checkUpdatesUrl: String?,

    @SerialName("displayURL")
    val homepageUrl: String,

    @SerialName("logoFile")
    val iconPath: String,

    @SerialName("authors")
    val developer: String,

    val description: String,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeModConfigEntry =
            NeoforgeModConfigEntry(
                id = mod.id,
                version = mod.version,
                name = mod.name,
                checkUpdatesUrl = null,
                homepageUrl = mod.getModrinthUrl().toString(),
                iconPath = mod.iconPath,
                developer = mod.repo.owner.developer,
                description = mod.description,
            )
    }
}
