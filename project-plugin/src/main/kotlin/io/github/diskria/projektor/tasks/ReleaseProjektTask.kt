package io.github.diskria.projektor.tasks

import io.github.diskria.projektor.ProjektorGradlePlugin
import org.gradle.api.DefaultTask
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Releasing involves external side effects like Git commits and tags")
abstract class ReleaseProjektTask : DefaultTask() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }
}
