package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.extensions.signing
import io.github.diskria.projektor.projekt.AndroidLibrary
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.MavenCentralShield
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

data object MavenCentral : LocalMaven() {

    override val shouldCreatePublication: Boolean = true

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
                        ", but got " + projekt::class.className()
            )
        }
        with(publication) {
            from(components[componentName])
            pom {
                name.set(projekt.name)
                description.set(projekt.description)
                url.set(projekt.getRepoUrl())
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
                        projekt.developer.let { developer ->
                            id.set(developer)
                            name.set(developer)
                        }
                        email.set(projekt.email)
                    }
                }
                scm {
                    url.set(projekt.getRepoUrl())
                    connection.set(
                        projekt.repoHost.versionControlSystem.buildScmUri(
                            projekt.getRepoUrl(isVcs = true)
                        )
                    )
                    developerConnection.set(
                        projekt.repoHost.versionControlSystem.buildScmUri(
                            projekt.repoHost.sshAuthority, projekt.getRepoPath(isVcs = true)
                        )
                    )
                }
            }
        }
        val gpgKey = Secrets.gpgKey.toNullIfEmpty() ?: return
        val gpgPassphrase = Secrets.gpgPassphrase.toNullIfEmpty() ?: return
        signing {
            useInMemoryPgpKeys(gpgKey, gpgPassphrase)
            sign(publication)
        }
    }

    override fun getReadmeShield(projekt: IProjekt): ReadmeShield =
        MavenCentralShield(projekt)
}
