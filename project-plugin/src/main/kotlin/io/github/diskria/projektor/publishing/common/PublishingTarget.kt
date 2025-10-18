package io.github.diskria.projektor.publishing.common

import io.github.diskria.gradle.utils.extensions.registerTask
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.tasks.release.common.ReleaseTask
import org.gradle.api.Project
import org.gradle.api.Task

abstract class PublishingTarget {

    abstract fun configurePublishing(projekt: IProjekt, project: Project)

    abstract fun configureReleaseTask(project: Project): Task

    abstract fun getConfigurePublicationTaskName(): String

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null

    fun configure(projekt: IProjekt, project: Project) {
        configurePublishing(projekt, project)
        val releaseTask = configureReleaseTask(project)
        project.registerTask<ReleaseTask> {
            targetTaskName.set(releaseTask.name)
        }
    }
}
