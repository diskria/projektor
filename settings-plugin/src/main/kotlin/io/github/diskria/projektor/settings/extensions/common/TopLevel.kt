package io.github.diskria.projektor.settings.extensions.common

import org.gradle.api.GradleException

fun gradleError(message: String): Nothing =
    throw GradleException(message)
