package io.github.diskria.projektor.projekt.metadata

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.projekt.common.IProjekt
import kotlinx.serialization.Serializable

@Serializable
@EncodeDefaults
@PrettyPrint
data class GithubMetadata(
    val homepage: String? = null,
    val description: String,
    val topics: Set<String>,
) {
    companion object {
        fun of(projekt: IProjekt): GithubMetadata =
            GithubMetadata(
                description = projekt.description,
                topics = buildSet {
                    val typeName = projekt::class.className().setCase(PascalCase, `kebab-case`)
                    add(typeName)
                    addAll(projekt.getGithubTopics())
                    addAll(projekt.tags)
                    projekt.publishingTarget?.getTypeName()?.setCase(PascalCase, `kebab-case`)?.let {
                        add(it)
                    }
                },
            )
    }
}
