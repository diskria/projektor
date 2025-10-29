package io.github.diskria.projektor.common.minecraft.sync.common

import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import kotlinx.serialization.Serializable

@Serializable
@PrettyPrint
class MinecraftArtifacts(
    val artifacts: List<MinecraftArtifact>,
    val lastSyncMillis: Long,
)
