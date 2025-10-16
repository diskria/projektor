package io.github.diskria.projektor.projekt.metadata

import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.common.IProjekt

data class LicenseMetadata(
    val license: License,
    val developer: String,
) {
    companion object {
        fun of(projekt: IProjekt): LicenseMetadata =
            LicenseMetadata(
                license = projekt.license,
                developer = projekt.developer,
            )
    }
}
