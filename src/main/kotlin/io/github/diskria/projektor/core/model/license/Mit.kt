package io.github.diskria.projektor.core.model.license

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import java.time.Year

internal object Mit : License("MIT") {

    override fun getPlaceholders(metadata: ProjektMetadata): Map<String, String> =
        mapOf(
            "year" to Year.now().value.toString(),
            "copyright holders" to metadata.repo.owner.developer,
        )
}
