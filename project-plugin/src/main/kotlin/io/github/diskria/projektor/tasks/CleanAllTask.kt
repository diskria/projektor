package io.github.diskria.projektor.tasks

import io.github.diskria.gradle.utils.extensions.clean
import io.github.diskria.gradle.utils.extensions.dependsSequentiallyOn
import io.github.diskria.gradle.utils.extensions.getTask
import org.gradle.api.DefaultTask

abstract class CleanAllTask : DefaultTask() {

    init {
        group = "build"
        dependsSequentiallyOn(
            listOf(
                project.getTask<CleanIncludedBuildsTask>(),
                project.getTask<CleanSubprojectsTask>(),
                project.tasks.clean.get(),
            )
        )
    }
}
