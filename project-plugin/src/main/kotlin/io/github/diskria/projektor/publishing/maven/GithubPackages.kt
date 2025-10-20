package io.github.diskria.projektor.publishing.maven

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.Environment
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.maven.common.MavenPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPackagesShield
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

data object GithubPackages : MavenPublishingTarget() {

    override fun configureMaven(
        repositories: RepositoryHandler,
        projekt: Projekt,
        project: Project,
    ): MavenArtifactRepository = with(repositories) {
        maven(projekt.repo.getPackagesMavenUrl()) {
            name = getRepositoryName()
            if (Environment.isCI()) {
                credentials {
                    username = projekt.repo.owner.developer
                    password = Environment.Secrets.githubPackagesToken
                }
            }
        }
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("github.com") {
            path(metadata.repo.owner.name, metadata.repo.name, "packages")
        }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GithubPackagesShield(metadata)
}
