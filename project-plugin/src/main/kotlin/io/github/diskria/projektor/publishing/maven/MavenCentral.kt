package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`Sentence case`
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.Environment
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.extensions.signing
import io.github.diskria.projektor.projekt.AndroidLibrary
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.maven.common.LocalMavenBasedPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.MavenCentralShield
import io.github.diskria.projektor.tasks.distribute.UploadBundleToMavenCentralTask
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

data object MavenCentral : LocalMavenBasedPublishingTarget() {

    override val shouldCreatePublication: Boolean = true

    override fun configurePublication(publication: MavenPublication, projekt: Projekt, project: Project) {
        val repository = projekt.repository
        with(publication) {
            from(project.components[projekt.getComponentName()])
            pom {
                name.set(projekt.name)
                description.set(projekt.description)
                url.set(repository.getUrl())
                licenses {
                    license {
                        projekt.license.let { license ->
                            name.set(license.id)
                            url.set(license.url)
                        }
                    }
                }
                developers {
                    developer {
                        repository.owner.developerName.let { developerName ->
                            id.set(developerName)
                            name.set(developerName)
                        }
                        email.set(repository.owner.email)
                    }
                }
                scm {
                    url.set(repository.getUrl())
                    connection.set(repository.buildScmUri(repository.getUrl(isVcs = true)))
                    developerConnection.set(
                        repository.buildScmUri(repository.getSshAuthority(), repository.getPath(isVcs = true))
                    )
                }
            }
        }
        if (Environment.isCI()) {
            with(project) {
                signing {
                    useInMemoryPgpKeys(Environment.Secrets.gpgKey, Environment.Secrets.gpgPassphrase)
                    sign(publication)
                }
            }
        }
    }

    override fun getHomepage(metadata: ProjektMetadataExtra): String =
        buildUrl("central.sonatype.com") {
            path("artifact", metadata.repository.owner.namespace, metadata.repository.name)
        }

    override fun configureDistributeTask(project: Project): Task =
        project.rootProject.ensureTaskRegistered<UploadBundleToMavenCentralTask>()

    override fun getReadmeShield(metadata: ProjektMetadataExtra): ReadmeShield =
        MavenCentralShield(metadata)

    private fun Projekt.getComponentName(): String =
        when (this) {
            is KotlinLibrary -> "java"
            is AndroidLibrary -> "release"
            else -> gradleError(
                "Only Kotlin library and Android library projects supported for publishing to Maven Central" +
                        ", but got " + type.getName(`Sentence case`)
            )
        }
}
