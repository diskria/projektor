package io.github.diskria.projektor.publishing.maven.common

import io.github.diskria.gradle.utils.extensions.isRootProject
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.publishing
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

abstract class MavenPublishingTarget : PublishingTarget() {

    open val shouldCreatePublication: Boolean = false

    open fun configurePublication(publication: MavenPublication, projekt: IProjekt, project: Project) {

    }

    abstract fun configureMaven(
        repositories: RepositoryHandler,
        projekt: IProjekt,
        project: Project
    ): MavenArtifactRepository

    override fun configurePublishing(projekt: IProjekt, project: Project) = with(project) {
        publishing {
            val fixedArtifactId = projekt.metadata.repository.name.modifyUnless(isRootProject()) {
                it + Constants.Char.HYPHEN + name
            }
            if (shouldCreatePublication) {
                publications.create<MavenPublication>(projekt.metadata.repository.name) {
                    configurePublication(this, projekt, project)
                }
            }
            publications.withType<MavenPublication> {
                artifactId = fixedArtifactId
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
