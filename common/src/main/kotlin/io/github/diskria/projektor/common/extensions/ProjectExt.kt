package io.github.diskria.projektor.common.extensions

import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

fun Project.setMetadata(metadata: io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata) {
    val projektMetadata by metadata.autoNamedProperty()
    extra[projektMetadata.name] = projektMetadata.value
}

fun Project.getMetadata(): io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata {
    val projektMetadata: io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata by rootProject.extra.properties
    return projektMetadata
}

fun Project.setProjektMetadata(metadata: io.github.diskria.projektor.common.metadata.ProjektMetadata) {
    val projektMetadata by metadata.autoNamedProperty()
    extra[projektMetadata.name] = projektMetadata.value
}

fun Project.getProjektMetadata(): io.github.diskria.projektor.common.metadata.ProjektMetadata {
    val projektMetadata: io.github.diskria.projektor.common.metadata.ProjektMetadata by rootProject.extra.properties
    return projektMetadata
}
