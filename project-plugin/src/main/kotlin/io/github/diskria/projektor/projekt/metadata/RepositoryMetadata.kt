package io.github.diskria.projektor.projekt.metadata

import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import kotlinx.serialization.Serializable

@Serializable
@EncodeDefaults
@PrettyPrint
data class RepositoryMetadata(
    val homepage: String? = null,
    val description: String,
    val topics: Set<String>,
)
