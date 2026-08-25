package io.github.diskria.projektor.extensions

import org.gradle.api.Task

internal fun Task.applyProjektorGroup() {
    group = "projektor"
}
