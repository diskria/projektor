package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.features.generation.readme.shields.live.GithubPackagesShield
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

internal object GithubPackages : MavenPublishingTarget("github-packages") {

    override fun configureRepository(
        project: Project,
        projekt: Projekt,
        repositories: RepositoryHandler,
        configure: MavenArtifactRepository.() -> Unit,
    ): MavenArtifactRepository =
        if (!project.providers.isCI) super.configureRepository(project, projekt, repositories, configure)
        else repositories.maven(projekt.repo.getPackagesMavenUrl()) {
            configure()
            val secrets = SecretsHelper(project.providers)
            credentials {
                it.username = projekt.repo.owner.developer
                it.password = secrets.githubPackagesToken
            }
        }

    override fun getHomepage(projekt: Projekt): String =
        "https://github.com/${projekt.repo.owner.name}/${projekt.repo.name}/packages"

    override fun getReadmeShield(projekt: Projekt) = GithubPackagesShield(projekt)
}
