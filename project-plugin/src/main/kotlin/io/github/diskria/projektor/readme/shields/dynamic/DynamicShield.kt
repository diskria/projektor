package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.publishing.common.PublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.ktor.http.*

sealed class DynamicShield(
    val pathParts: List<String>,
    val extraParameters: List<Pair<String, String?>> = emptyList(),
    publishingTarget: PublishingTarget,
    url: String
) : ReadmeShield(
    label = publishingTarget.mapToEnum().getName(`Title Case`),
    url = url,
) {
    override val urlBuilder: URLBuilder.() -> Unit
        get() = {
            val segments = pathParts.toTypedArray()
            segments[segments.lastIndex] = segments.last() + ".svg"
            path(*segments)
            extraParameters.forEach { (name, value) ->
                parameters.append(name, value.orEmpty())
            }
        }

    override fun getAlt(): String =
        label
}
