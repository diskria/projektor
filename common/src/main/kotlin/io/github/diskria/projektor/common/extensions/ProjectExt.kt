package io.github.diskria.projektor.common.extensions

import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromString
import io.github.diskria.kotlin.utils.extensions.serialization.serializeToString
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

fun Project.setProjektMetadata(metadata: ProjektMetadata) {
    val projektMetadata by metadata.serializeToString().autoNamedProperty()
    rootProject.extra[projektMetadata.name] = projektMetadata.value
}

fun Project.getProjektMetadata(): ProjektMetadata {
    val projektMetadata: String by rootProject.extra.properties
    return projektMetadata.deserializeFromString()
}
