package io.github.diskria.projektor.tasks

import io.github.diskria.gradle.utils.extensions.dependsOnIncludedBuilds
import org.gradle.api.DefaultTask

abstract class CleanIncludedBuildsTask : DefaultTask() {

    init {
        group = "build"
        dependsOnIncludedBuilds()
    }
}
