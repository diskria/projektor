package io.github.diskria.projektor.core.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ProjektModule(
    val path: String,
    val type: ProjektType,
    val name: String,
)
