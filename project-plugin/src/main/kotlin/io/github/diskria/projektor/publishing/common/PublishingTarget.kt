package io.github.diskria.projektor.publishing.common

import io.github.diskria.gradle.utils.extensions.getTaskPath
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjectModules
import io.github.diskria.projektor.extensions.getLeafProjects
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishingTarget {

    abstract fun getPublishTaskName(project: Project): String

    abstract fun configurePublishTask(projekt: Projekt, project: Project): Boolean

    abstract fun getHomepage(metadata: ProjektMetadata): String

    open fun configureDistributeTask(rootProject: Project): Task? = null

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null

    fun configureRootPublishTask(project: Project, rootProject: Project, publishTask: Task): Task =
        registerRootPublishTask(project, rootProject).apply {
            configure {
                group = publishTask.group
                description = publishTask.description

                rootProject.getLeafProjects().forEach {
                    if (it.path != ProjectModules.Common.PATH) {
                        dependsOn(it.getTaskPath(getPublishTaskName(it)))
                    }
                }
            }
        }.get()

    protected abstract fun registerRootPublishTask(project: Project, rootProject: Project): TaskProvider<out Task>
}
