package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.register
import io.github.diskria.projektor.features.distribution.tasks.DeployMavenToGithubPagesTask
import io.github.diskria.projektor.features.generation.readme.GithubPagesShield
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import io.github.diskria.projektor.generated.EnvProvider
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal object GithubPagesDistributionTarget : MavenDistributionTarget(DistributionTargetType.GITHUB_PAGES) {

    override fun configureDistributeTask(project: Project, projekt: Projekt.Distributable): TaskProvider<out Task> {
        val publishTask = configurePublishTask(project, projekt)
        return project.tasks.register<DeployMavenToGithubPagesTask>(EnvProvider(project.providers)) {
            repo.set(projekt.metadata.repo)
            dependsOn(publishTask)
        }
    }

    override fun getHomepage(projekt: Projekt.Distributable): String = projekt.metadata.repo.pagesUrl
    override fun getReadmeShield(projekt: Projekt.Distributable): ReadmeShield = GithubPagesShield(projekt)
}
