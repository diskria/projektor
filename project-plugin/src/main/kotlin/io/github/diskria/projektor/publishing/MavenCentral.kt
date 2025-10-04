package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.kotlin.runExtension
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.*
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
        val gpgKey = Secrets.gpgKey
        val gpgPassphrase = Secrets.gpgPassphrase
        if (gpgKey == null || gpgPassphrase == null) {
            println("Skipping Maven Central publishing configuration: GPG keys are missing")
            return@configure
        }
        val componentName = when (projekt) {
            is AndroidLibrary -> "release"
            is KotlinLibrary -> "java"
            else -> error(
                "Publishing to Maven Central is supported only for Library or AndroidLibrary projects, but got " +
                        projekt::class.className()
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
                    name.set(projekt.repo)
                    description.set(projekt.description)
                    url.set(projekt.getRepoUrl())
                    licenses {
                        license {
                            projekt.license.let { license ->
                                name.set(license.displayName)
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
                        connection.set(projekt.repoHost.vcs.buildUri(projekt.getRepoUrl(true)))
                        developerConnection.set(
                            projekt.repoHost.vcs.buildUri(projekt.repoHost.sshAuthority, projekt.getRepoPath(true))
                        )
                    }
                }
            }
        }
        runExtension<SigningExtension> {
            useInMemoryPgpKeys(gpgKey, gpgPassphrase)
            sign(publication)
        }
    }
}
