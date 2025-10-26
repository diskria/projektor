package io.github.diskria.projektor.publishing.external.common

import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class ExternalPublishingTarget : PublishingTarget() {

    override fun registerRootPublishTask(project: Project, rootProject: Project): TaskProvider<out Task> =
        rootProject.tasks.register(getPublishTaskName(project))
}
