package io.github.diskria.projektor.core.model.license

import java.time.Year

internal object Mit : License("MIT") {

    override fun getPlaceholders(developer: String): Map<String, String> =
        mapOf(
            "year" to Year.now().value.toString(),
            "copyright holders" to developer,
        )
}
