package io.github.diskria.projektor.common.extensions

import io.github.diskria.gradle.utils.extensions.put
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

fun Project.setMetadata(metadata: ProjektMetadata) {
    val projektMetadata by metadata.autoNamedProperty()
    extra.put(projektMetadata)
}

fun Project.getMetadata(): ProjektMetadata {
    val projektMetadata: ProjektMetadata by rootProject.extra.properties
    return projektMetadata
}
