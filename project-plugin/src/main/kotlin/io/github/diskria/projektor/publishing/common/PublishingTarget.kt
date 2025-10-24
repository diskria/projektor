package io.github.diskria.projektor.publishing.common

import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjektModules
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishingTarget {

    abstract val publishTaskName: String

    abstract fun configure(projekt: Projekt, project: Project)

    abstract fun getHomepage(metadata: ProjektMetadata): String

    open fun configureDistributeTask(rootProject: Project): Task? = null

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null

    fun configureRootPublishTask(rootProject: Project, childPublishTask: Task): Task =
        registerRootPublishTask(rootProject).apply {
            configure {
                group = childPublishTask.group
                description = childPublishTask.description

                rootProject.childProjects.values.filterNot { it.path == ProjektModules.COMMON_PATH }.forEach {
                    dependsOn(":${it.name}:$publishTaskName")
                }
            }
        }.get()

    protected abstract fun registerRootPublishTask(rootProject: Project): TaskProvider<out Task>
}
