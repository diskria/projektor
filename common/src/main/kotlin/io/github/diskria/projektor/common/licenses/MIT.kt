package io.github.diskria.projektor.common.licenses

import io.github.diskria.projektor.common.projekt.ProjektMetadata
import java.time.Year

object MIT : License("MIT") {

    override fun getPlaceholders(metadata: ProjektMetadata): Map<String, String> =
        mapOf(
            "year" to Year.now().value.toString(),
            "copyright holders" to metadata.developer,
        )
}
