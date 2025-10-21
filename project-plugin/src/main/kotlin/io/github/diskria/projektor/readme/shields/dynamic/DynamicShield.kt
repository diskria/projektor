package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.modifyLast
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.getHomepages
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.ktor.http.*

sealed class DynamicShield(
    val metadata: ProjektMetadata,
    val extraParameters: List<Pair<String, String?>> = emptyList(),
) : ReadmeShield(metadata.publishingTargets.first().getName(`Title Case`), metadata.getHomepages().first()) {

    abstract fun getPathParts(): List<String>

    override val urlBuilder: URLBuilder.() -> Unit
        get() = {
            path(
                *getPathParts()
                    .toMutableList()
                    .modifyLast { fileName(it, Constants.File.Extension.SVG) }
                    .toTypedArray()
            )
            extraParameters.forEach { (name, value) ->
                parameters.append(name, value.orEmpty())
            }
        }

    override fun getAlt(): String =
        label
}
