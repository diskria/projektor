package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.extensions.capitalized
import io.github.diskria.projektor.extensions.registerIfAbsent
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal sealed class MavenDistributionTarget(
    private val distributionTargetType: DistributionTargetType
) : DistributionTarget {

    private val repositoryName: String = distributionTargetType.id.split('-').joinToString("") { it.capitalized() }

    fun getLocalMavenDirectory(layout: ProjectLayout): Provider<Directory> =
        layout.buildDirectory.dir("maven/${distributionTargetType.id}")

    open fun configureRepository(
        project: Project,
        projekt: Projekt.Distributable,
        repositories: RepositoryHandler,
        configure: (MavenArtifactRepository) -> Unit
    ): MavenArtifactRepository =
        repositories.maven { repository ->
            configure(repository)
            repository.setUrl(getLocalMavenDirectory(project.layout))
        }

    open fun configureSigning(
        project: Project,
        projekt: Projekt,
        publications: DomainObjectCollection<MavenPublication>,
    ) {
    }

    override fun configureDistributeTasks(project: Project, projekt: Projekt.Distributable): List<String> =
        configurePublishTask(project, projekt)

    protected fun configurePublishTask(project: Project, projekt: Projekt.Distributable): List<String> {
        val componentName = checkNotNull(projekt.softwareComponent) {
            "This kind of project doesn't support publishing to ${distributionTargetType.displayName}"
        }
        project.pluginManager.apply("maven-publish")
        project.extensions.configure<PublishingExtension> {
            configureRepository(project, projekt, repositories) { repository ->
                repository.name = repositoryName
            }
        }
        val container = project.extensions.getByType<PublishingExtension>().publications
        val mavenPublications = container.withType<MavenPublication>()
        val publicationNames = if (projekt is GradlePlugin) {
            val pluginPublicationName = "pluginMaven"
            val publicationNames = listOf(pluginPublicationName, "${projekt.internalName}PluginMarkerMaven")
            val pluginPublications = mavenPublications.matching { it.name in publicationNames }
            pluginPublications.configureEach { publication ->
                if (publication.pom.url.isPresent) return@configureEach
                configurePom(publication.pom, projekt)
                if (publication.name == pluginPublicationName) {
                    publication.artifactId = projekt.name
                }
            }
            configureSigning(project, projekt, pluginPublications)
            publicationNames
        } else {
            val publicationName = "${projekt.internalName}Maven"
            container.registerIfAbsent<MavenPublication>(publicationName) { publication ->
                configurePom(publication.pom, projekt)
                publication.artifactId = projekt.name
                publication.from(project.components[componentName])
            }
            configureSigning(project, projekt, mavenPublications.matching { it.name == publicationName })
            listOf(publicationName)
        }
        return publicationNames.map { "publish${it.capitalized()}PublicationTo${repositoryName}Repository" }
    }

    private fun configurePom(pom: MavenPom, projekt: Projekt.Distributable) {
        val repo = projekt.metadata.repo
        val organizationUrl = repo.owner.organizationUrl
        pom.name.set(projekt.displayName)
        pom.description.set(projekt.description)
        pom.url.set(repo.url)
        organizationUrl?.let {
            pom.organization { org ->
                org.name.set(repo.owner.name)
                org.url.set(it)
            }
        }
        pom.scm { scm ->
            scm.url.set(repo.url)
            scm.connection.set(repo.scmUrl)
            scm.developerConnection.set(repo.scmDeveloperUrl)
        }
        pom.issueManagement { issues ->
            issues.system.set("GitHub Issues")
            issues.url.set(repo.issuesUrl)
        }
        pom.ciManagement { ci ->
            ci.system.set("GitHub Actions")
            ci.url.set(repo.actionsUrl)
        }
        pom.developers { spec ->
            spec.developer { dev ->
                dev.id.set(repo.owner.developer)
                dev.name.set(repo.owner.developer)
                dev.email.set(repo.owner.email)
                dev.url.set(repo.owner.profileUrl)
                organizationUrl?.let {
                    dev.organization.set(repo.owner.name)
                    dev.organizationUrl.set(it)
                }
            }
        }
        projekt.metadata.licenseType?.mapToModel()?.let { projektLicense ->
            pom.licenses { spec ->
                spec.license { license ->
                    license.name.set(projektLicense.type.id)
                    license.url.set(projektLicense.url)
                }
            }
        }
    }
}
