package io.github.diskria.projektor.common.minecraft.sync.jfrog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JFrogChild(
    val uri: String,

    @SerialName("folder")
    val isFolder: Boolean,
)
