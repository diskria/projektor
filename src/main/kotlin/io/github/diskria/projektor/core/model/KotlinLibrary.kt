package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.api.KotlinLibraryDsl

internal class KotlinLibrary(projekt: Projekt, val configuration: KotlinLibraryDsl) : AbstractProjekt(projekt) {
    override val softwareComponent: String get() = "java"
    override val version: String get() = configuration.version.orElse(super.version).get()
    override val javaVersion: Int get() = configuration.javaVersion.orElse(super.javaVersion).get()
    override val jvmTarget: Int get() = configuration.jvmTarget.orElse(super.jvmTarget).get()
}
