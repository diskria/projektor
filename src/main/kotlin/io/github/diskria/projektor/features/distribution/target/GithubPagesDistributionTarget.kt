package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.registerTask
import io.github.diskria.projektor.features.distribution.tasks.DeployMavenToGithubPagesTask
import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.features.generation.readme.shields.live.GithubPagesShield
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal object GithubPagesDistributionTarget : MavenDistributionTarget("github-pages") {

    override fun configureDistributeTask(project: Project, projekt: Projekt): TaskProvider<out Task> {
        val publishTask = configurePublishTask(project, projekt)
        return project.tasks.registerTask<DeployMavenToGithubPagesTask>(SecretsHelper(project.providers)) {
            dependsOn(publishTask)
            mustRunAfter(publishTask)
        }
    }

    override fun getHomepage(projekt: Projekt): String = projekt.metadata.repo.pagesUrl
    override fun getReadmeShield(projekt: Projekt): ReadmeShield = GithubPagesShield(projekt)
}
