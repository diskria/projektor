package io.github.diskria.projektor.minecraft.configs.forge

import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.minecraft.versions.VersionBound
import io.github.diskria.projektor.minecraft.versions.range.IntervalVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
open class ForgeModDependencyConfigEntry(
    @SerialName("modId")
    val id: String,

    @SerialName("mandatory")
    val isRequired: Boolean,

    @SerialName("versionRange")
    val version: String,

    val ordering: String,
    val side: String,
) {
    companion object {
        fun of(id: String, version: String): ForgeModDependencyConfigEntry =
            ForgeModDependencyConfigEntry(
                id = id,
                isRequired = true,
                version = version,
                ordering = "NONE",
                side = "BOTH",
            )

        fun createMinecraftDependency(mod: MinecraftMod): ForgeModDependencyConfigEntry =
            of(
                id = "minecraft",
                version = IntervalVersionRange.min(VersionBound.inclusive(mod.minecraftVersion.asString())),
            )

        fun createLoaderDependency(mod: MinecraftMod): ForgeModDependencyConfigEntry =
            of(
                id = "forge",
                version = IntervalVersionRange.min(VersionBound.inclusive(mod.config.forge.loader)),
            )
    }
}
