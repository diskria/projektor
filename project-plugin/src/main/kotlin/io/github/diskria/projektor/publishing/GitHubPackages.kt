package io.github.diskria.projektor.publishing

import io.github.diskria.projektor.extensions.kotlin.publishing
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.projekt.IProjekt
import io.github.diskria.projektor.projekt.Secrets
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType

data object GitHubPackages : PublishingTarget {

    override val configurePublishing: Project.(IProjekt) -> Unit = configure@{ projekt ->
        val githubPackagesToken = Secrets.githubPackagesToken
        if (githubPackagesToken == null) {
            println("Skipping Github Packages publishing configuration: token is missing")
            return@configure
        }
        publishing {
            publications.withType<MavenPublication> {
                artifactId = projekt.slug
            }
            repositories {
                maven(GithubProfile.getPackagesMavenUrl(projekt.slug)) {
                    name = "GitHubPackages"
                    credentials {
                        username = GithubProfile.username
                        password = githubPackagesToken
                    }
                }
            }
        }
    }
}
