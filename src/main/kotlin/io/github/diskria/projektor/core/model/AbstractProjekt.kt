package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata

internal abstract class AbstractProjekt(private val base: Projekt) : Projekt {
    override val metadata: ProjektMetadata get() = base.metadata
    override val license get() = base.license
    override val distributionTargets get() = base.distributionTargets
    override val softwareComponent get() = base.softwareComponent
    override val packageName: String get() = metadata.packageName
    override val displayName: String get() = metadata.displayName
    override val version: String get() = metadata.version
    override val description: String get() = metadata.description
    override val tags: Set<String> get() = metadata.tags
}
