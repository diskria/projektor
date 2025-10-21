package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.common.repository.RepositoryHost
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
            if (EnvironmentHelper.isCI()) {
                credentials {
                    username = projekt.repo.owner.developer
                    password = Secrets.githubPackagesToken
                }
            }
        }
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl(RepositoryHost.GITHUB.hostName) {
            path(metadata.repo.owner.name, metadata.repo.name, "packages")
        }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GithubPackagesShield(metadata)
}
