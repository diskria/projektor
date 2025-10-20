package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.projekt.common.Projekt

fun ProjektMetadataExtra.getHomepage(): String =
    publishingTarget.mapToModel().getHomepage(this)
