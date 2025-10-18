package io.github.diskria.projektor.publishing.maven

import io.github.diskria.projektor.Environment
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.maven.common.MavenPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPackagesShield
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

data object GithubPackages : MavenPublishingTarget() {

    override fun configureMaven(
        repositories: RepositoryHandler,
        projekt: IProjekt,
        project: Project,
    ): MavenArtifactRepository = with(repositories) {
        maven(projekt.metadata.repository.getPackagesMavenUrl()) {
            name = getRepositoryName()
            if (Environment.isCI()) {
                credentials {
                    username = projekt.metadata.repository.owner.developerName
                    password = Environment.Secrets.githubPackagesToken
                }
            }
        }
    }

    override fun configureReleaseTask(project: Project) = TODO()

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GithubPackagesShield(metadata.repository)
}
