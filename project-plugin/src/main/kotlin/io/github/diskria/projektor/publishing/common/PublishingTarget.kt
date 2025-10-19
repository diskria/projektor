package io.github.diskria.projektor.publishing.common

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.tasks.ReleaseTask
import org.gradle.api.Project
import org.gradle.api.Task

abstract class PublishingTarget {

    fun configure(projekt: IProjekt, project: Project) {
        configurePublishing(projekt, project)
        val distributeTask = configureDistributeTask(project)
        project.rootProject.ensureTaskRegistered<ReleaseTask> {
            publishTaskName.set(getPublishTaskName())
            distributeTaskName.set(distributeTask.name)
        }
    }

    abstract fun configurePublishing(projekt: IProjekt, project: Project)

    abstract fun configureDistributeTask(project: Project): Task

    abstract fun getPublishTaskName(): String

    abstract fun getHomepage(metadata: ProjektMetadata): String

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null
}
