package io.github.diskria.projektor.core.model.license

import io.github.diskria.projektor.ProjektorGradlePlugin

internal sealed class License(val type: LicenseType) {

    val url = "https://spdx.org/licenses/${type.id}"

    open fun getPlaceholders(developer: String): Map<String, String> = emptyMap()

    fun getLicenseText(developer: String): String {
        val template = ProjektorGradlePlugin.readResourceText("licenses/${type.id}.txt")
        return getPlaceholders(developer).entries.fold(template) { acc, (name, value) -> acc.replace("<$name>", value) }
    }
}
