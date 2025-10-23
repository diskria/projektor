package io.github.diskria.projektor.publishing.maven.common

import io.github.diskria.gradle.utils.extensions.isRootProject
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.publishing
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

abstract class MavenPublishingTarget : PublishingTarget() {

    open val shouldCreatePublication: Boolean = false

    open fun configurePublication(publication: MavenPublication, projekt: Projekt, project: Project) {

    }

    abstract fun configureMaven(
        repositories: RepositoryHandler,
        projekt: Projekt,
        project: Project
    ): MavenArtifactRepository

    override fun configure(projekt: Projekt, project: Project) = with(project) {
        publishing {
            val artifactId = projekt.repo.name.modifyUnless(isRootProject()) {
                it + Constants.Char.HYPHEN + name
            }
            if (shouldCreatePublication) {
                publications.create<MavenPublication>(projekt.repo.name) {
                    configurePublication(this, projekt, project)
                }
            }
            publications.withType<MavenPublication> {
                this.artifactId = artifactId
                if (!shouldCreatePublication) {
                    configurePublication(this, projekt, project)
                }
            }
            configureMaven(repositories, projekt, project)
        }
    }

    override fun getPublishTaskName(): String =
        "publishAllPublicationsTo${getRepositoryName()}Repository"

    protected fun getRepositoryName(): String =
        mapToEnum().getName(PascalCase)
}
