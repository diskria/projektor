package io.github.diskria.projektor.projekt.common

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.publishing.common.PublishingTarget

data class Projekt(
    override val metadata: ProjektMetadata,
    override val license: License,
    override val publishingTarget: PublishingTarget,
    override val javaVersion: Int,
    override val kotlinVersion: String,
) : IProjekt
