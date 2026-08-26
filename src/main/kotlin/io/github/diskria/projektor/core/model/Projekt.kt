package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.features.distribution.target.DistributionTarget
import io.github.diskria.projektor.internal.utils.capitalized

internal interface Projekt {
    val metadata: ProjektMetadata
    val packageName: String
    val displayName: String
    val version: String
    val description: String
    val tags: Set<String>
    val license: License
    val softwareComponent: String? get() = null
    val distributionTargets: List<DistributionTarget>
    val isSourcesEnabled: Boolean get() = true
    val isJavadocEnabled: Boolean get() = true
    val javaVersion: Int get() = 25
    val jvmTarget: Int get() = 17
    val classNamePrefix: String get() = metadata.repo.name.split("-").joinToString("") { it.capitalized() }
}
