package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.features.generation.readme.shields.live.GithubPackagesShield
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

internal object GithubPackages : MavenPublishingTarget("github-packages") {

    override fun getRepositoryUrl(project: Project, projekt: Projekt): Any = projekt.repo.getPackagesMavenUrl()

    override fun configureRepository(project: Project, projekt: Projekt, repository: MavenArtifactRepository) {
        with(repository) {
            if (!project.providers.isCI) return
            val secrets = SecretsHelper(project.providers)
            credentials {
                it.username = projekt.repo.owner.developer
                it.password = secrets.githubPackagesToken
            }
        }
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        "https://github.com/${metadata.repo.owner.name}/${metadata.repo.name}/packages"

    override fun getReadmeShield(metadata: ProjektMetadata) = GithubPackagesShield(metadata)
}
