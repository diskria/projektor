package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal sealed interface DistributionTarget {
    fun configureDistributeTask(project: Project, projekt: Projekt.Regular): TaskProvider<out Task>
    fun getHomepage(projekt: Projekt): String? = null
    fun getReadmeShield(projekt: Projekt): ReadmeShield? = null
}
