package io.github.diskria.projektor.core.model

import kotlinx.serialization.Serializable as JsonSerializable
import java.io.Serializable as PropertySerializable

@JsonSerializable
class ProjektModule(
    val path: String,
    val type: ProjektType,
    val name: String,
) : PropertySerializable {
    val isRoot: Boolean get() = path == ":"
}
