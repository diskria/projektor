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

    abstract fun configurePublishTask(projekt: Projekt, project: Project): Task?

    abstract fun getHomepage(metadata: ProjektMetadata): String

    open fun configureDistributeTask(rootProject: Project): Task? = null

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null

    fun configureRootPublishTask(rootProject: Project, publishTask: Task): Task =
        registerRootPublishTask(rootProject).apply {
            configure {
                group = publishTask.group
                description = publishTask.description

                val leafProjects = generateSequence(listOf(rootProject)) { parents ->
                    parents.flatMap { it.childProjects.values }.takeIf { it.isNotEmpty() }
                }.last()
                leafProjects.forEach {
                    if (it.path == ProjektModules.COMMON_PATH) {
                        return@forEach
                    }
                    dependsOn("${it.path}:$publishTaskName")
                }
            }
        }.get()

    protected abstract fun registerRootPublishTask(rootProject: Project): TaskProvider<out Task>
}
