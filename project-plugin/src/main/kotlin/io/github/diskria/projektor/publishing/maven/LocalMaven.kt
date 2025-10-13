package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.maven.common.MavenPublishingTarget
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

abstract class LocalMaven : MavenPublishingTarget() {

    override fun configureMaven(
        repositories: RepositoryHandler,
        projekt: IProjekt,
        project: Project,
    ): MavenArtifactRepository = with(repositories) {
        maven(project.getBuildDirectory(DIRECTORY_NAME)) {
            name = getTypeName()
        }
    }

    companion object {
        const val DIRECTORY_NAME: String = "localMaven"
    }
}
