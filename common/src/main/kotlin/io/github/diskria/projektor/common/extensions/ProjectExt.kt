package io.github.diskria.projektor.common.extensions

import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

fun Project.setProjektMetadata(metadata: ProjektMetadata) {
    val projektMetadata by metadata.autoNamedProperty()
    extra[projektMetadata.name] = projektMetadata.value
}

fun Project.getProjektMetadata(): ProjektMetadata {
    val projektMetadata: ProjektMetadata by rootProject.extra.properties
    return projektMetadata
}
