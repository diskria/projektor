package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel

fun ProjektMetadata.getHomepage(): String =
    publishingTarget.mapToModel().getHomepage(this)
