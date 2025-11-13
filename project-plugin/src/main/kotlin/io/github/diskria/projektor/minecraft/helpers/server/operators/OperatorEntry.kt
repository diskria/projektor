package io.github.diskria.projektor.minecraft.helpers.server.operators

import kotlinx.serialization.Serializable

@Serializable
data class OperatorEntry(
    val uuid: String,
    val name: String,
)
