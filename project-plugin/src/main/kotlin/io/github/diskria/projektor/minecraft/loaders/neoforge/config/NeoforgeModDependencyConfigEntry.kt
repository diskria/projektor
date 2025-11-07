package io.github.diskria.projektor.minecraft.loaders.neoforge.config

import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.minecraft.versions.VersionBound
import io.github.diskria.projektor.minecraft.versions.range.IntervalVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
open class NeoforgeModDependencyConfigEntry(
    @SerialName("modId")
    val id: String,

    val type: String,

    @SerialName("versionRange")
    val version: String,

    val ordering: String,
    val side: String,
) {
    companion object {
        fun of(id: String, version: String): NeoforgeModDependencyConfigEntry =
            NeoforgeModDependencyConfigEntry(
                id = id,
                type = "required",
                version = version,
                ordering = "NONE",
                side = "BOTH",
            )

        fun createMinecraftDependency(mod: MinecraftMod): NeoforgeModDependencyConfigEntry =
            of(
                id = "minecraft",
                version = IntervalVersionRange.min(VersionBound.inclusive(mod.minecraftVersion.asString())),
            )

        fun createLoaderDependency(mod: MinecraftMod): NeoforgeModDependencyConfigEntry =
            of(
                id = "neoforge",
                version = IntervalVersionRange.min(VersionBound.inclusive(mod.config.neoforge.loader)),
            )
    }
}
