package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.features.publishing.target.PublishingTarget
import io.github.diskria.projektor.internal.utils.capitalized

internal interface Projekt {
    val metadata: ProjektMetadata
    val repo: GithubRepo
    val packageName: String
    val name: String
    val version: String
    val description: String
    val tags: Set<String>
    val license: License
    val softwareComponent: String? get() = null
    val publishingTargets: List<PublishingTarget>
    val isSourcesEnabled: Boolean get() = true
    val isJavadocEnabled: Boolean get() = true
    val javaVersion: Int get() = 25
    val jvmTarget: Int get() = 17
    val archiveName: String get() = repo.name
    val classNamePrefix: String get() = repo.name.split("-").joinToString("") { it.capitalized() }
}
