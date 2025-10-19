package io.github.diskria.projektor.tasks

import io.github.diskria.projektor.ProjektorGradlePlugin
import org.gradle.api.DefaultTask

abstract class ReleaseTask : DefaultTask() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }
}
