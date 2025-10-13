package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.isRootProject
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendSuffix
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.projektor.extensions.publishing
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.PublishingTarget
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.withType

abstract class Maven : PublishingTarget {

    fun getArtifactId(projekt: IProjekt, project: Project): String =
        projekt.repo.modifyUnless(project.isRootProject()) {
            it.appendSuffix(Constants.Char.HYPHEN + project.name)
        }

    open fun configurePublication(publication: MavenPublication, projekt: IProjekt, project: Project) {

    }

    abstract fun configureMaven(
        repositories: RepositoryHandler,
        projekt: IProjekt,
        project: Project
    ): MavenArtifactRepository

    override fun configure(projekt: IProjekt, project: Project) = with(project) {
        publishing {
            publications.withType<MavenPublication> {
                artifactId = getArtifactId(projekt, project)
                configurePublication(this, projekt, project)
            }
            configureMaven(repositories, projekt, project)
        }
    }
}
