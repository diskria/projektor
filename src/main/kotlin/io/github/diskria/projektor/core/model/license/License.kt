package io.github.diskria.projektor.core.model.license

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata

internal sealed class License(val id: String) {

    val templateUrl = "https://raw.githubusercontent.com/spdx/license-list-data/main/text/$id.txt"
    val url = "https://spdx.org/licenses/$id"

    open fun getPlaceholders(metadata: ProjektMetadata): Map<String, String> = emptyMap()

    fun fillTemplate(template: String, metadata: ProjektMetadata): String =
        getPlaceholders(metadata).entries.fold(template) { acc, (name, value) -> acc.replace("<$name>", value) }
}
