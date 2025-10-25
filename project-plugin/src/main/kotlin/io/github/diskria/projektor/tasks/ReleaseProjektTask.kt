package io.github.diskria.projektor.tasks

import io.github.diskria.projektor.ProjektorGradlePlugin
import org.gradle.api.DefaultTask

abstract class ReleaseProjektTask : DefaultTask() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }
}
