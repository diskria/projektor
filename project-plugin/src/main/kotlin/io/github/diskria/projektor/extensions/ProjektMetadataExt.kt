package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel

fun ProjektMetadata.getHomepages(): List<String> =
    publishingTargets.map { it.mapToModel().getHomepage(this) }
