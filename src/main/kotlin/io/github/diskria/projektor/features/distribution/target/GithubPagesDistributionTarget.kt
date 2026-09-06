package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.register
import io.github.diskria.projektor.features.distribution.tasks.DeployMavenToGithubPagesTask
import io.github.diskria.projektor.features.generation.readme.GithubPagesShield
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import org.gradle.api.Project

internal object GithubPagesDistributionTarget : MavenDistributionTarget(DistributionTargetType.GITHUB_PAGES) {

    override fun configureDistributeTasks(project: Project, projekt: Projekt.Distributable): List<String> {
        val publishTask = configurePublishTask(project, projekt)
        val distributeTask = project.tasks.register<DeployMavenToGithubPagesTask> { task ->
            task.repo.set(projekt.metadata.repo)
            task.dependsOn(publishTask)
        }
        return listOf(distributeTask.name)
    }

    override fun getHomepage(projekt: Projekt.Distributable): String = projekt.metadata.repo.pagesUrl
    override fun getReadmeShield(projekt: Projekt.Distributable): ReadmeShield = GithubPagesShield(projekt)
}
