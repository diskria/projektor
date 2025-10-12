package io.github.diskria.projektor.projekt.metadata

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
data class PublishingMetadata(
    val target: String? = null
) {
    companion object {
        fun of(projekt: IProjekt): PublishingMetadata =
            PublishingMetadata(
                target = projekt.publishingTarget?.getTypeName()?.setCase(PascalCase, `kebab-case`),
            )
    }
}
