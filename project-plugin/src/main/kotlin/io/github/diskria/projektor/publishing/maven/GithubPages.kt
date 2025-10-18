package io.github.diskria.projektor.publishing.maven

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.maven.common.LocalMavenBasedPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPagesShield
import io.github.diskria.projektor.tasks.release.ReleaseToGithubPagesTask
import org.gradle.api.Project
import org.gradle.api.Task

data object GithubPages : LocalMavenBasedPublishingTarget() {

    override fun configurePublishing(projekt: IProjekt, project: Project) {
        super.configurePublishing(projekt, project)
    }

    override fun configureReleaseTask(project: Project): Task =
        project.ensureTaskRegistered<ReleaseToGithubPagesTask>()

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GithubPagesShield(metadata.repository)
}
