package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.DistributionTargetType.*
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal sealed interface DistributionTarget {
    fun configureDistributeTask(project: Project, projekt: Projekt.Distributable): TaskProvider<out Task>
    fun getHomepage(projekt: Projekt.Distributable): String? = null
    fun getReadmeShield(projekt: Projekt.Distributable): ReadmeShield? = null
}

internal fun DistributionTargetType.mapToModel(): DistributionTarget = when (this) {
    MAVEN_LOCAL -> MavenLocalDistributionTarget
    MAVEN_CENTRAL -> MavenCentralDistributionTarget
    GITHUB_PAGES -> GithubPagesDistributionTarget
    GITHUB_PACKAGES -> GithubPackagesDistributionTarget
    GRADLE_PLUGIN_PORTAL -> GradlePluginPortalDistributionTarget
}
