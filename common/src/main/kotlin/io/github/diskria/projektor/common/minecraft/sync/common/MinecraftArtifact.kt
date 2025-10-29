package io.github.diskria.projektor.common.minecraft.sync.common

import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionSerializer
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftArtifact(
    @Serializable(with = MinecraftVersionSerializer::class)
    val minecraftVersion: MinecraftVersion,

    val artifactVersion: String,
)
