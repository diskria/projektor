package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType

data object GitHubPackages : PublishingTarget {

    override val configure: Project.(IProjekt) -> Unit = configure@{ projekt ->
        val githubPackagesToken = Secrets.githubPackagesToken.toNullIfEmpty() ?: return@configure
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
