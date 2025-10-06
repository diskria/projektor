package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.projekt.AndroidLibrary
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven
import org.gradle.plugins.signing.SigningExtension

data object MavenCentral : PublishingTarget {

    override val configure: Project.(IProjekt) -> Unit = configure@{ projekt ->
        val componentName = when (projekt) {
            is AndroidLibrary -> "release"
            is KotlinLibrary -> "java"
            else -> gradleError(
                "Only Kotlin/Android library projects supported for publishing to Maven Central" +
                        ", but got " + projekt::class.className()
            )
        }
        runExtension<PublishingExtension> {
            repositories {
                maven(getBuildDirectory("staging-repo").get().asFile) {
                    name = "MavenCentral"
                }
            }
        }
        val publication = runExtension<PublishingExtension, Publication> {
            publications.create<MavenPublication>(projekt.repo) {
                artifactId = projekt.repo
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
        }
        val gpgKey = Secrets.gpgKey
        val gpgPassphrase = Secrets.gpgPassphrase
        if (gpgKey != null && gpgPassphrase != null) {
            runExtension<SigningExtension> {
                useInMemoryPgpKeys(gpgKey, gpgPassphrase)
                sign(publication)
            }
        }
    }
}
