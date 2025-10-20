package io.github.diskria.projektor.licenses

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import java.time.Year

object Mit : License("MIT") {

    override fun getPlaceholders(metadata: ProjektMetadata): Map<String, String> =
        mapOf(
            "year" to Year.now().value.toString(),
            "copyright holders" to metadata.repo.owner.developer,
        )
}
