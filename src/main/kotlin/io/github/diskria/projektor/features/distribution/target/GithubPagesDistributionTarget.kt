package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.projektMetadata
import io.github.diskria.projektor.extensions.registerTask
import io.github.diskria.projektor.features.distribution.tasks.DeployMavenToGithubPagesTask
import io.github.diskria.projektor.features.generation.readme.GithubPagesShield
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal object GithubPagesDistributionTarget : MavenDistributionTarget(DistributionTargetType.GITHUB_PAGES) {

    override fun configureDistributeTask(project: Project, projekt: Projekt.Regular): TaskProvider<out Task> {
        val publishTask = configurePublishTask(project, projekt)
        return project.tasks.registerTask<DeployMavenToGithubPagesTask>(SecretsHelper(project.providers)) {
            repo.set(project.rootProject.projektMetadata.repo)
            repoDirectory.convention(project.layout.projectDirectory)
            from(getLocalMavenDirectory(project))
            into(project.layout.projectDirectory.dir("docs"))
            dependsOn(publishTask)
        }
    }

    override fun getHomepage(projekt: Projekt): String = projekt.metadata.repo.pagesUrl
    override fun getReadmeShield(projekt: Projekt): ReadmeShield = GithubPagesShield(projekt)
}
