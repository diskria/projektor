package io.github.diskria.projektor.licenses

import io.github.diskria.projektor.projekt.common.IProjekt
import java.time.Year

object MIT : License("MIT") {

    override fun getPlaceholders(projekt: IProjekt): Map<String, String> =
        mapOf(
            "year" to Year.now().value.toString(),
            "copyright holders" to projekt.owner,
        )
}
