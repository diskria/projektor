package io.github.diskria.projektor.common.extensions

import io.github.diskria.gradle.utils.extensions.put
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

fun Project.putMetadataExtra(metadata: ProjektMetadataExtra) {
    val projektMetadata by metadata.autoNamedProperty()
    extra.put(projektMetadata)
}

fun Project.getMetadataExtra(): ProjektMetadataExtra {
    val projektMetadata: ProjektMetadataExtra by rootProject.extra.properties
    return projektMetadata
}

fun Project.setMetadata(metadata: ProjektMetadata) {
    val projektMetadata by metadata.autoNamedProperty()
    extra.put(projektMetadata)
}

fun Project.getMetadata(): ProjektMetadata {
    val projektMetadata: ProjektMetadata by rootProject.extra.properties
    return projektMetadata
}
