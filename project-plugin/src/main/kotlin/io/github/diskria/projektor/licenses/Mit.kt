package io.github.diskria.projektor.licenses

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import java.time.Year

object Mit : License("MIT") {

    override fun getPlaceholders(metadata: ProjektMetadataExtra): Map<String, String> =
        mapOf(
            "year" to Year.now().value.toString(),
            "copyright holders" to metadata.repository.owner.developerName,
        )
}
