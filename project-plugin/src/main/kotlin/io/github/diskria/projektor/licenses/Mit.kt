package io.github.diskria.projektor.licenses

import io.github.diskria.projektor.projekt.metadata.LicenseMetadata
import java.time.Year

object Mit : License("MIT") {

    override fun getPlaceholders(metadata: LicenseMetadata): Map<String, String> =
        mapOf(
            "year" to Year.now().value.toString(),
            "copyright holders" to metadata.developer,
        )
}
