package io.github.diskria.projektor.tasks

import io.github.diskria.gradle.utils.extensions.dependsOnSubprojects
import org.gradle.api.DefaultTask

abstract class CleanSubprojectsTask : DefaultTask() {

    init {
        group = "build"
        dependsOnSubprojects()
    }
}
