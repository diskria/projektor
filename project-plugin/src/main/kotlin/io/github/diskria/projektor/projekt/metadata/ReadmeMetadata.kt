package io.github.diskria.projektor.projekt.metadata

import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield

data class ReadmeMetadata(
    val name: String,
    val description: String,
    val shields: List<ReadmeShield>,
    val license: License,
) {
    companion object {
        fun of(projekt: IProjekt): ReadmeMetadata =
            ReadmeMetadata(
                name = projekt.name,
                description = projekt.description,
                shields = projekt.getReadmeShields(),
                license = projekt.license,
            )
    }
}
