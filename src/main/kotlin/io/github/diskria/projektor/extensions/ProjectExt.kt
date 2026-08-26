package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.internal.utils.Errors
import org.gradle.api.Project

fun Project.ensureRootProject(): Project =
    if (this == rootProject) this
    else Errors.internal.error("Expected root project, but was called on subproject '$path'")
