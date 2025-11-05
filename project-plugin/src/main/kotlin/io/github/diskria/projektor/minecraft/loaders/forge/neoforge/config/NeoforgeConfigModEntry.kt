package io.github.diskria.projektor.minecraft.loaders.forge.neoforge.config

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeoforgeConfigModEntry(
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
    val icon: String,

    val credits: String?,
    val authors: String,
    val description: String,
) {
    companion object {
        fun of(mod: MinecraftMod): NeoforgeConfigModEntry =
            NeoforgeConfigModEntry(
                id = mod.id,
                version = mod.version,
                name = mod.name,
                checkUpdatesUrl = null,
                homepageUrl = mod.publishingTargets
                    .first { it.mapToEnum() == PublishingTargetType.MODRINTH }
                    .getHomepage(mod.metadata)
                    .toString(),
                icon = buildPath("assets", mod.id, fileName("icon", Constants.File.Extension.PNG)),
                credits = null,
                authors = mod.repo.owner.developer,
                description = mod.description,
            )
    }
}
