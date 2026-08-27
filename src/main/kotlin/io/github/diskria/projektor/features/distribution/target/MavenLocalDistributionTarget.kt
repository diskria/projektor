package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

internal object MavenLocalDistributionTarget : MavenDistributionTarget(DistributionTargetType.MAVEN_LOCAL) {

    override fun configureRepository(
        project: Project,
        projekt: Projekt,
        repositories: RepositoryHandler,
        configure: MavenArtifactRepository.() -> Unit
    ): MavenArtifactRepository = repositories.mavenLocal(configure)
}
