package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal sealed interface PublishingTarget {
    fun configurePublishTask(project: Project, projekt: Projekt): TaskProvider<out Task>
    fun configureDistributeTask(project: Project): TaskProvider<out Task>? = null
    fun getHomepage(metadata: ProjektMetadata): String
    fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null
}
