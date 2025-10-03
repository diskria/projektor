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

    override val publish: Project.(IProjekt) -> Unit = configure@{ projekt ->
        val gpgKey = Secrets.gpgKey
        val gpgPassphrase = Secrets.gpgPassphrase
        if (gpgKey == null || gpgPassphrase == null) {
            println("Skipping Maven Central publishing configuration: GPG keys are missing")
            return@configure
        }
        val componentName = when (projekt) {
            is AndroidLibrary -> "release"
            is KotlinLibrary -> "java"
            else -> error("Publishing to Maven Central is supported only for Library or AndroidLibrary projects, but got ${projekt::class.className()}")
        }
        runExtension<PublishingExtension> {
            repositories {
                maven(getBuildDirectory("staging-repo").get().asFile) {
                    name = "MavenCentral"
                }
            }
        }
        val publication = runExtension<PublishingExtension, Publication> {
            publications.create<MavenPublication>(projekt.slug) {
                artifactId = projekt.slug
                from(components[componentName])
                pom {
                    name.set(projekt.name)
                    description.set(projekt.description)
                    url.set(projekt.owner.getRepositoryUrl(projekt.slug))
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
                            projekt.owner.name.let { ownerName ->
                                id.set(ownerName)
                                name.set(ownerName)
                            }
                            email.set(projekt.owner.email)
                        }
                    }
                    scm {
                        url.set(projekt.owner.getRepositoryUrl(projekt.slug))
                        connection.set(
                            projekt.scm.buildUri(projekt.owner.getRepositoryUrl(projekt.slug, isVcsUrl = true))
                        )
                        developerConnection.set(
                            projekt.scm.buildUri(
                                projekt.softwareForge.getSshAuthority(),
                                projekt.owner.getRepositoryPath(projekt.slug, isVcsUrl = true)
                            )
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
