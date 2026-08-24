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
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.maven

internal sealed class MavenPublishingTarget(val id: String) : PublishingTarget {

    val repositoryName: String = id.split("-").joinToString("") { it.capitalized() }

    fun getLocalMavenDirectory(project: Project): Provider<Directory> = project.layout.buildDirectory.dir("maven/$id")

    open fun getRepositoryUrl(project: Project, projekt: Projekt): Any = getLocalMavenDirectory(project)
    open fun configureRepository(project: Project, projekt: Projekt, repository: MavenArtifactRepository) {}
    open fun configurePublication(project: Project, projekt: Projekt, publication: MavenPublication) {}

    override fun configurePublishTask(project: Project, projekt: Projekt): TaskProvider<out Task> = with(project) {
        val componentName = Errors.frontend.checkNotNull(projekt.softwareComponent) {
            "This kind of project doesn't support publishing to Maven"
        }
        val publicationName = projekt.repo.name.split("-").withIndex().joinToString("") { (index, part) ->
            if (index == 0) part else part.capitalized()
        }
        ensurePluginApplied("maven-publish")
        publishing {
            with(repositories) {
                maven(getRepositoryUrl(project, projekt)) {
                    name = repositoryName
                    configureRepository(project, projekt, this)
                }
            }
            val publication = with(publications) {
                maybeCreate<MavenPublication>(publicationName) {
                    from(Errors.internal.checkNotNull(components.findByName(componentName)) {
                        "SoftwareComponent '$componentName' not found in project '$name'"
                    })
                }
            }
            configurePublication(project, projekt, publication)
        }
        return tasks.named("publishAllPublicationsTo${repositoryName}Repository")
    }
}
