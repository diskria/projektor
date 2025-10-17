package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.Environment
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.extensions.signing
import io.github.diskria.projektor.projekt.AndroidLibrary
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.maven.common.LocalMaven
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.MavenCentralShield
import io.github.diskria.projektor.tasks.release.ReleaseToMavenCentralTask
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

data object MavenCentral : LocalMaven() {

    override val shouldCreatePublication: Boolean = true

    override fun configure(projekt: IProjekt, project: Project) {
        super.configure(projekt, project)
        project.rootProject.ensureTaskRegistered<ReleaseToMavenCentralTask>()
    }

    override fun configurePublication(
        publication: MavenPublication,
        projekt: IProjekt,
        project: Project
    ) = with(project) {
        val componentName = when (projekt) {
            is KotlinLibrary -> "java"
            is AndroidLibrary -> "release"
            else -> gradleError(
                "Only Kotlin/Android library projects supported for publishing to Maven Central" +
                        ", but got " + projekt.metadata.type.getName(`kebab-case`)
            )
        }
        val repository = projekt.metadata.repository
        with(publication) {
            from(components[componentName])
            pom {
                name.set(projekt.metadata.name)
                description.set(projekt.metadata.description)
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
            signing {
                useInMemoryPgpKeys(Environment.Secrets.gpgKey, Environment.Secrets.gpgPassphrase)
                sign(publication)
            }
        }
    }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        MavenCentralShield(metadata.repository)
}
