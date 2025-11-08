package io.github.diskria.projektor.minecraft.loaders.fabric.config

import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.minecraft.versions.VersionBound
import io.github.diskria.projektor.minecraft.versions.range.InequalityVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class FabricModDependenciesConfig private constructor(
    @SerialName("java")
    val javaVersion: String,

    @SerialName("minecraft")
    val minecraftVersion: String,

    @SerialName("fabricloader")
    val loaderVersion: String,
) {
    companion object {
        fun of(mod: MinecraftMod): FabricModDependenciesConfig =
            FabricModDependenciesConfig(
                javaVersion = InequalityVersionRange.min(VersionBound.inclusive(mod.jvmTarget.toInt().toString())),
                minecraftVersion = InequalityVersionRange.min(VersionBound.inclusive(mod.minecraftVersion.asString())),
                loaderVersion = InequalityVersionRange.min(
                    VersionBound.inclusive(
                        if (mod.loader.mapToEnum() == ModLoaderType.LEGACY_FABRIC) mod.config.legacyFabric.loader
                        else mod.config.fabric.loader
                    )
                ),
            )
    }
}
