package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.isProjektMavenPublicationConfigured
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.capitalized
import io.github.diskria.projektor.internal.utils.checkNotNull
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType

internal sealed class MavenDistributionTarget(val id: String) : DistributionTarget {

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

    override fun configureDistributeTask(project: Project, projekt: Projekt.Regular): TaskProvider<out Task> =
        configurePublishTask(project, projekt)

    protected fun configurePublishTask(project: Project, projekt: Projekt.Regular): TaskProvider<out Task> {
        val componentName = Errors.frontend.checkNotNull(projekt.softwareComponent) {
            "This kind of project doesn't support publishing to Maven"
        }
        project.pluginManager.apply("maven-publish")
        project.extensions.configure<PublishingExtension> {
            configureRepository(project, projekt, repositories) {
                name = repositoryName
            }
            val isGradlePlugin = projekt is GradlePlugin
            val publicationName = if (isGradlePlugin) {
                "pluginMaven"
            } else {
                projekt.metadata.repo.name.split("-").withIndex().joinToString("") { (index, part) ->
                    if (index == 0) part else part.capitalized()
                }
            }
            if (!isGradlePlugin) {
                val publication = publications.maybeCreate(publicationName, MavenPublication::class.java)
                publication.from(Errors.internal.checkNotNull(project.components.findByName(componentName)) {
                    "SoftwareComponent '$componentName' not found in project '${project.path}'"
                })
            }
            if (!project.isProjektMavenPublicationConfigured) {
                publications
                    .matching { it.name == publicationName }
                    .withType<MavenPublication>()
                    .configureEach { publication ->
                        if (project.isProjektMavenPublicationConfigured) return@configureEach
                        configurePom(publication.pom, projekt)
                        configurePublication(project, projekt, publication)
                        project.isProjektMavenPublicationConfigured = true
                    }
            }
        }
        return project.tasks.named("publishAllPublicationsTo${repositoryName}Repository")
    }

    private fun configurePom(pom: MavenPom, projekt: Projekt.Regular) {
        val repo = projekt.metadata.repo
        val organizationUrl = repo.owner.organizationUrl
        with(pom) {
            name.set(projekt.displayName)
            description.set(projekt.description)
            url.set(repo.url)
            organizationUrl?.let { organizationUrl ->
                organization {
                    it.name.set(repo.owner.name)
                    it.url.set(organizationUrl)
                }
            }
            scm {
                it.url.set(repo.url)
                it.connection.set(repo.scmUrl)
                it.developerConnection.set(repo.scmDeveloperUrl)
            }
            issueManagement {
                it.system.set("GitHub Issues")
                it.url.set(repo.issuesUrl)
            }
            ciManagement {
                it.system.set("GitHub Actions")
                it.url.set(repo.actionsUrl)
            }
            developers { spec ->
                spec.developer {
                    it.id.set(repo.owner.developer)
                    it.name.set(repo.owner.developer)
                    it.email.set(repo.owner.email)
                    it.url.set(repo.owner.profileUrl)
                    organizationUrl?.let { organizationUrl ->
                        it.organization.set(repo.owner.name)
                        it.organizationUrl.set(organizationUrl)
                    }
                }
            }
            projekt.license?.let { projektLicense ->
                licenses { spec ->
                    spec.license {
                        it.name.set(projektLicense.id)
                        it.url.set(projektLicense.url)
                    }
                }
            }
        }
    }
}
