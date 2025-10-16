package io.github.diskria.projektor.projekt.metadata

import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.common.IProjekt
import kotlinx.serialization.Serializable

@Serializable
@EncodeDefaults
@PrettyPrint
data class RepositoryMetadata(
    val homepage: String? = null,
    val description: String,
    val topics: Set<String>,
) {
    companion object {
        fun of(projekt: IProjekt): RepositoryMetadata =
            RepositoryMetadata(
                description = projekt.description,
                topics = buildSet {
                    add(projekt.typeName)
                    addAll(projekt.getGithubTopics())
                    addAll(projekt.tags)
                    projekt.publishingTarget?.mapToEnum()?.getName(`kebab-case`)?.let {
                        add(it)
                    }
                },
            )
    }
}
