package io.github.diskria.projektor.minecraft.loaders.forge.neoforge.config

import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.minecraft.versions.VersionBound
import io.github.diskria.projektor.minecraft.versions.range.IntervalVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class NeoforgeConfigDependencyEntry(
    @SerialName("modId")
    val id: String,

    val type: String,
    val versionRange: String,
    val ordering: String,
    val side: String,
) {
    companion object {
        fun of(id: String, versionRange: String): Pair<String, NeoforgeConfigDependencyEntry> =
            id to NeoforgeConfigDependencyEntry(
                id = id,
                type = "required",
                versionRange = versionRange,
                ordering = "NONE",
                side = "BOTH",
            )

        fun createMinecraftDependency(mod: MinecraftMod): Pair<String, NeoforgeConfigDependencyEntry> =
            of(
                "minecraft", IntervalVersionRange.range(
                    VersionBound.inclusive(mod.minSupportedVersion.asString()),
                    VersionBound.inclusive(mod.maxSupportedVersion.asString())
                )
            )

        fun createLoaderDependency(mod: MinecraftMod): Pair<String, NeoforgeConfigDependencyEntry> =
            of(
                "neoforge", IntervalVersionRange.min(
                    VersionBound.inclusive(mod.config.neoforge.loader)
                )
            )
    }
}
