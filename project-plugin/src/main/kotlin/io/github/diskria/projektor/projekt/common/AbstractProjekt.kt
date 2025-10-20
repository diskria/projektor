package io.github.diskria.projektor.projekt.common

import io.github.diskria.projektor.common.github.GithubRepo
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata

abstract class AbstractProjekt(private val base: Projekt) : Projekt {
    override val metadata: ProjektMetadata get() = base.metadata
    override val type: ProjektType get() = metadata.type
    override val repo: GithubRepo get() = metadata.repo
    override val packageNameBase: String get() = metadata.packageNameBase
    override val name: String get() = metadata.name
    override val version: String get() = metadata.version
    override val description: String get() = metadata.description
    override val tags: Set<String> get() = metadata.tags

    override val license get() = base.license
    override val publishingTarget get() = base.publishingTarget
    override val javaVersion get() = base.javaVersion
    override val kotlinVersion get() = base.kotlinVersion
}
