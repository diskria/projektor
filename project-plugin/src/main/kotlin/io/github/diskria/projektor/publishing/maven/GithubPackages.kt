package io.github.diskria.projektor.publishing.maven

import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPackageShield
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

data object GithubPackages : Maven() {

    override fun configureMaven(
        repositories: RepositoryHandler,
        projekt: IProjekt,
        project: Project,
    ): MavenArtifactRepository = with(repositories) {
        maven(projekt.githubPackagesUrl) {
            name = getTypeName()
            credentials {
                username = projekt.developer
                password = Secrets.githubPackagesToken.toNullIfEmpty()
            }
        }
    }

    override fun publish(projekt: IProjekt, project: Project) {

    }

    override fun getReadmeShield(projekt: IProjekt): ReadmeShield =
        GithubPackageShield(projekt)
}
