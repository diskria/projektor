package io.github.diskria.projektor.minecraft.loaders.ornithe.config

import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.minecraft.versions.VersionBound
import io.github.diskria.projektor.minecraft.versions.range.InequalityVersionRange
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class OrnitheModDependenciesConfig private constructor(
    @SerialName("java")
    val javaVersion: String,

    @SerialName("fabricloader")
    val loaderVersion: String,
) {
    companion object {
        fun of(mod: MinecraftMod): OrnitheModDependenciesConfig =
            OrnitheModDependenciesConfig(
                javaVersion = InequalityVersionRange.min(VersionBound.inclusive(mod.jvmTarget.toInt().toString())),
                loaderVersion = InequalityVersionRange.min(VersionBound.inclusive(mod.config.ornithe.loader)),
            )
    }
}
