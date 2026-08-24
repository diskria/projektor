package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.registerTask
import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.features.generation.readme.shields.live.GithubPagesShield
import io.github.diskria.projektor.features.publishing.tasks.DeployMavenToGithubPagesTask
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal object GithubPages : MavenPublishingTarget("github-pages") {

    override fun configureDistributeTask(project: Project): TaskProvider<out Task> =
        project.tasks.registerTask<DeployMavenToGithubPagesTask>(SecretsHelper(project.providers))

    override fun getHomepage(metadata: ProjektMetadata): String = metadata.repo.getPagesUrl()

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GithubPagesShield(metadata)
}
