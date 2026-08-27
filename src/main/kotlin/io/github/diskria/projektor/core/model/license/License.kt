package io.github.diskria.projektor.core.model.license

internal sealed class License(val id: String) {

    val templateUrl = "https://raw.githubusercontent.com/spdx/license-list-data/main/text/$id.txt"
    val url = "https://spdx.org/licenses/$id"

    open fun getPlaceholders(developer: String): Map<String, String> = emptyMap()

    fun fillTemplate(template: String, developer: String): String =
        getPlaceholders(developer).entries.fold(template) { acc, (name, value) -> acc.replace("<$name>", value) }
}
