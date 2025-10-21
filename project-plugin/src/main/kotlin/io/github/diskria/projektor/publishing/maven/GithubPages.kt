package io.github.diskria.projektor.publishing.maven

import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.publishing.maven.common.LocalMavenBasedPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPagesShield
import io.github.diskria.projektor.tasks.distribute.DeployMavenToGithubPagesTask
import org.gradle.api.Project
import org.gradle.api.Task

data object GithubPages : LocalMavenBasedPublishingTarget() {

    override fun getHomepage(metadata: ProjektMetadata): String =
        metadata.repo.getPagesUrl()

    override fun configureDistributeTask(project: Project): Task =
        project.rootProject.ensureTaskRegistered<DeployMavenToGithubPagesTask>()

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GithubPagesShield(metadata)
}
