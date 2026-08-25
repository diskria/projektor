package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.ensurePluginApplied
import io.github.diskria.projektor.extensions.maybeCreate
import io.github.diskria.projektor.extensions.publishing
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.capitalized
import io.github.diskria.projektor.internal.utils.checkNotNull
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.maven

internal sealed class MavenPublishingTarget(val id: String) : PublishingTarget {

    val repositoryName: String = id.split("-").joinToString("") { it.capitalized() }

    fun getLocalMavenDirectory(project: Project): Provider<Directory> =
        project.layout.buildDirectory.dir("maven/$id")

    open fun configureRepository(
        project: Project,
        projekt: Projekt,
        repositories: RepositoryHandler,
        configure: MavenArtifactRepository.() -> Unit,
    ): MavenArtifactRepository =
        repositories.maven(getLocalMavenDirectory(project), configure)

    open fun configurePublication(project: Project, projekt: Projekt, publication: MavenPublication) {}

    override fun configurePublishTask(project: Project, projekt: Projekt): TaskProvider<out Task> {
        val componentName = Errors.frontend.checkNotNull(projekt.softwareComponent) {
            "This kind of project doesn't support publishing to Maven"
        }
        val publicationName = projekt.repo.name.split("-").withIndex().joinToString("") { (index, part) ->
            if (index == 0) part else part.capitalized()
        }
        project.ensurePluginApplied("maven-publish")
        project.publishing {
            configureRepository(project, projekt, repositories) {
                name = repositoryName
            }
            val publication = publications.maybeCreate<MavenPublication>(publicationName) {
                from(Errors.internal.checkNotNull(project.components.findByName(componentName)) {
                    "SoftwareComponent '$componentName' not found in project '$name'"
                })
                val repoUrl = projekt.repo.getUrl()
                with(pom) {
                    name.set(projekt.displayName)
                    description.set(projekt.description)
                    url.set(repoUrl)
                    licenses { spec ->
                        spec.license {
                            it.name.set(projekt.license.id)
                            it.url.set(projekt.license.url)
                        }
                    }
                    developers { spec ->
                        spec.developer {
                            it.id.set(projekt.repo.owner.developer)
                            it.name.set(projekt.repo.owner.developer)
                            it.email.set(projekt.repo.owner.email)
                        }
                    }
                    scm {
                        it.url.set(repoUrl)
                        it.connection.set(projekt.repo.getScmConnectionUrl())
                        it.developerConnection.set(projekt.repo.getScmDeveloperConnectionUrl())
                    }
                }
            }
            configurePublication(project, projekt, publication)
        }
        return project.tasks.named("publishAllPublicationsTo${repositoryName}Repository")
    }
}
