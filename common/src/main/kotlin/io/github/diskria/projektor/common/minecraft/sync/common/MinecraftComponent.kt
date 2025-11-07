package io.github.diskria.projektor.common.minecraft.sync.common

import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersionSerializer
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftComponent(
    @Serializable(with = MinecraftVersionSerializer::class)
    val minecraftVersion: MinecraftVersion,

    val latestVersion: String,
)
