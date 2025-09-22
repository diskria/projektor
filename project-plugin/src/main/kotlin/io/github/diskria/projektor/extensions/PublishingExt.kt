package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.extensions.common.gradleError
import io.github.diskria.projektor.owner.GithubOwner
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.projekt.*
import io.github.diskria.utils.kotlin.extensions.common.className
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

fun <R> Project.signing(block: SigningExtension.() -> R): R =
    getExtensionOrThrow<SigningExtension>().block()

fun <R> Project.publishing(block: PublishingExtension.() -> R): R =
    getExtensionOrThrow<PublishingExtension>().block()

fun Project.configurePublishing(projekt: IProjekt, target: PublishingTarget?) {
    if (target == null) {
        println("Publishing target is null, skip.")
        return
    }
    when (target) {
        PublishingTarget.GITHUB_PACKAGES -> configureGithubPackagesPublishing(projekt)
        PublishingTarget.GITHUB_PAGES -> configureGithubPagesPublishing(projekt)
        PublishingTarget.MAVEN_CENTRAL -> configureMavenCentralPublishing(projekt)
        PublishingTarget.MODRINTH -> configureModrinthPublishing(projekt)
        PublishingTarget.GRADLE_PLUGIN_PORTAL -> {}
        PublishingTarget.GOOGLE_PLAY -> configureGooglePlayPublishing(projekt)
    }
}

private fun Project.configureGithubPackagesPublishing(projekt: IProjekt) {
    val githubOwner = projekt.owner as? GithubOwner ?: gradleError(
        "Attempted to configure publishing to GitHub Packages, " +
                "but the owner type is ${projekt.owner::class.className()}"
    )
    val githubPackagesToken = Secrets.githubPackagesToken
    if (githubPackagesToken == null) {
        println("Skipping Github Packages publishing configuration: token is missing")
        return
    }
    publishing {
        publications.withType<MavenPublication> {
            artifactId = projekt.slug
        }
        repositories {
            maven(githubOwner.getPackagesMavenUrl(projekt.slug)) {
                name = PublishingTarget.GITHUB_PACKAGES.mavenName()
                credentials {
                    username = GithubProfile.username
                    password = githubPackagesToken
                }
            }
        }
    }
}

private fun Project.configureGithubPagesPublishing(projekt: IProjekt) {
    publishing {
        repositories {
            maven(buildDirectory("repo")) {
                name = PublishingTarget.GITHUB_PAGES.mavenName()
            }
        }
    }
}

private fun Project.configureMavenCentralPublishing(project: IProjekt) {
    val gpgKey = Secrets.gpgKey
    val gpgPassphrase = Secrets.gpgPassphrase
    if (gpgKey == null || gpgPassphrase == null) {
        println("Skipping Maven Central publishing configuration: GPG keys are missing")
        return
    }
    publishing {
        repositories {
            maven(buildDirectory("staging-repo").get().asFile) {
                name = PublishingTarget.MAVEN_CENTRAL.mavenName()
            }
        }
    }
    val publication = publishing {
        publications.create<MavenPublication>(project.slug) {
            artifactId = project.slug
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set(project.owner.getRepositoryUrl(project.slug))
                licenses {
                    license {
                        project.license.let {
                            name.set(it.displayName)
                            url.set(it.getUrl())
                        }
                    }
                }
                developers {
                    developer {
                        project.owner.name.let {
                            id.set(it)
                            name.set(it)
                        }
                        email.set(project.owner.email)
                    }
                }
                scm {
                    url.set(project.owner.getRepositoryUrl(project.slug))
                    connection.set(
                        project.scm.buildUri(project.owner.getRepositoryUrl(project.slug, isVcsUrl = true))
                    )
                    developerConnection.set(
                        project.scm.buildUri(
                            project.softwareForge.getSshAuthority(),
                            project.owner.getRepositoryPath(project.slug, isVcsUrl = true)
                        )
                    )
                }
            }
        }
    }
    signing {
        useInMemoryPgpKeys(gpgKey, gpgPassphrase)
        sign(publication)
    }
}
