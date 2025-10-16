package io.github.diskria.projektor.publishing.maven

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.maven.common.LocalMaven
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPagesShield
import io.github.diskria.projektor.tasks.release.ReleaseToGithubPagesTask
import org.gradle.api.Project

data object GithubPages : LocalMaven() {

    override fun configure(projekt: IProjekt, project: Project) {
        super.configure(projekt, project)
        project.rootProject.ensureTaskRegistered<ReleaseToGithubPagesTask>()
    }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GithubPagesShield(metadata.repository)
}
