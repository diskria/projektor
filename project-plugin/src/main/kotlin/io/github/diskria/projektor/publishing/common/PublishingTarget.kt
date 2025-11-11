package io.github.diskria.projektor.publishing.common

import io.github.diskria.gradle.utils.extensions.getLeafProjects
import io.github.diskria.gradle.utils.extensions.getTaskPath
import io.github.diskria.gradle.utils.extensions.isCommonProject
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishingTarget {

    abstract val publishTaskName: String

    abstract fun configurePublishTask(project: Project, projekt: Projekt): Boolean

    abstract fun getHomepage(metadata: ProjektMetadata): Url

    open fun configureDistributeTask(rootProject: Project): Task? = null

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null

    fun configureRootPublishTask(rootProject: Project, publishTask: Task, projekt: Projekt): Task =
        registerRootPublishTask(rootProject).apply {
            configure {
                group = publishTask.group
                description = publishTask.description

                rootProject
                    .getLeafProjects { subproject ->
                        when {
                            subproject.isCommonProject() -> false
                            projekt is MinecraftMod -> ModSide.values().map { it.getName() }.contains(subproject.name)
                            else -> true
                        }
                    }
                    .forEach { dependsOn(it.getTaskPath(publishTaskName)) }
            }
        }.get()

    protected abstract fun registerRootPublishTask(rootProject: Project): TaskProvider<out Task>
}
