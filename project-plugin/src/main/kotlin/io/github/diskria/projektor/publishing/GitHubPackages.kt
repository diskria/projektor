package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.kotlin.runExtension
import io.github.diskria.projektor.projekt.IProjekt
import io.github.diskria.projektor.projekt.Secrets
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType

data object GitHubPackages : PublishingTarget {

    override val configure: Project.(IProjekt) -> Unit = configure@{ projekt ->
        val githubPackagesToken = Secrets.githubPackagesToken
        if (githubPackagesToken == null) {
            println("Skipping Github Packages publishing configuration: token is missing")
            return@configure
        }
        runExtension<PublishingExtension> {
            publications.withType<MavenPublication> {
                artifactId = projekt.repo
            }
            repositories {
                maven(projekt.githubPackagesUrl) {
                    name = "GitHubPackages"
                    credentials {
                        username = projekt.developer
                        password = githubPackagesToken
                    }
                }
            }
        }
    }
}
