package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.features.generation.readme.GithubPackagesShield
import io.github.diskria.projektor.generated.EnvProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

internal object GithubPackagesDistributionTarget : MavenDistributionTarget(DistributionTargetType.GITHUB_PACKAGES) {

    override fun configureRepository(
        project: Project,
        projekt: Projekt.Distributable,
        repositories: RepositoryHandler,
        configure: MavenArtifactRepository.() -> Unit
    ): MavenArtifactRepository {
        val env = EnvProvider(project.providers)
        if (!env.isCI) {
            return super.configureRepository(project, projekt, repositories, configure)
        }
        return repositories.maven(projekt.metadata.repo.packagesMavenUrl) {
            configure()
            credentials {
                it.username = projekt.metadata.repo.owner.developer
                it.password = env.githubPackagesToken
            }
        }
    }

    override fun getHomepage(projekt: Projekt.Distributable): String = projekt.metadata.repo.packagesUrl
    override fun getReadmeShield(projekt: Projekt.Distributable) = GithubPackagesShield(projekt)
}
