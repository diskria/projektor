package io.github.diskria.projektor.extensions

import org.gradle.api.Project

fun Project.ensureRootProject(): Project =
    if (this == rootProject) this
    else error("Expected root project, but was called on subproject '$path'")
