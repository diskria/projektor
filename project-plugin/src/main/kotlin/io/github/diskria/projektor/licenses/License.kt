package io.github.diskria.projektor.licenses

import io.github.diskria.kotlin.utils.BracketsType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.foldChain
import io.github.diskria.kotlin.utils.extensions.wrapWithBrackets
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.ktor.http.*

sealed class License(val id: String) {

    val templateUrl: Url
        get() = buildUrl("raw.githubusercontent.com") {
            path("spdx", "license-list-data", "main", "text", fileName(id, Constants.File.Extension.TXT))
        }

    val url: Url
        get() = buildUrl("spdx.org") {
            path("licenses", id)
        }

    open fun getPlaceholders(metadata: ProjektMetadata): Map<String, String> = emptyMap()

    fun fillTemplate(template: String, metadata: ProjektMetadata): String =
        getPlaceholders(metadata).entries.foldChain(template) { (name, value) ->
            replace(name.wrapWithBrackets(BracketsType.ANGLE), value)
        }
}
