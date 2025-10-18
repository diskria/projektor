package io.github.diskria.projektor.publishing.maven.common

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

abstract class LocalMavenBasedPublishingTarget : MavenPublishingTarget() {

    override fun configureMaven(
        repositories: RepositoryHandler,
        projekt: IProjekt,
        project: Project,
    ): MavenArtifactRepository = with(repositories) {
        maven(project.getBuildDirectory(LOCAL_MAVEN_DIRECTORY_NAME)) {
            name = getRepositoryName()
        }
    }

    companion object {
        const val LOCAL_MAVEN_DIRECTORY_NAME: String = "localMaven"
    }
}