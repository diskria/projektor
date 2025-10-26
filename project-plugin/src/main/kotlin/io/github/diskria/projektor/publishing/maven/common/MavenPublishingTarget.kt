package io.github.diskria.projektor.publishing.maven.common

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.isRootProject
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.publishing
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

abstract class MavenPublishingTarget : PublishingTarget() {

    protected val repositoryName: String
        get() = mapToEnum().getName(PascalCase)

    open val shouldCreatePublication: Boolean = false

    open fun configurePublication(projekt: Projekt, project: Project, publication: MavenPublication) {

    }

    fun getLocalMavenDirectory(project: Project): Provider<Directory> =
        project.getBuildDirectory("maven/" + this::class.className().setCase(PascalCase, `kebab-case`))

    open fun configureMaven(
        repositories: RepositoryHandler,
        projekt: Projekt,
        project: Project,
    ): MavenArtifactRepository = with(repositories) {
        maven(getLocalMavenDirectory(project)) {
            name = repositoryName
        }
    }

    override fun getPublishTaskName(project: Project): String =
        "publishAllPublicationsTo${repositoryName}Repository"

    override fun registerRootPublishTask(project: Project, rootProject: Project): TaskProvider<out Task> =
        rootProject.tasks.register<Sync>(getPublishTaskName(project)) {
            rootProject.childProjects.values.forEach { from(getLocalMavenDirectory(it)) }
            into(getLocalMavenDirectory(rootProject))
        }

    override fun configurePublishTask(projekt: Projekt, project: Project): Boolean = with(project) {
        val artifactId = projekt.repo.name.modifyUnless(isRootProject()) {
            it + Constants.Char.HYPHEN + name
        }
        publishing {
            if (shouldCreatePublication) {
                publications.create<MavenPublication>(projekt.repo.name) {
                    configurePublication(projekt, project, this)
                }
            }
            publications.withType<MavenPublication> {
                this.artifactId = artifactId
                if (!shouldCreatePublication) {
                    configurePublication(projekt, project, this)
                }
            }
            configureMaven(repositories, projekt, project)
        }
        return true
    }
}
