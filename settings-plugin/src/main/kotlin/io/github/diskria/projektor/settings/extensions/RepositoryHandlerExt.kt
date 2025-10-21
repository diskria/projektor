package io.github.diskria.projektor.settings.extensions

import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.repo.github.GithubRepo
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.configureMaven(
    name: String,
    url: String,
    group: String? = null,
    includeSubgroups: Boolean = true,
): MavenArtifactRepository =
    maven(url) {
        this.name = name
    }.also { repository ->
        if (group == null) {
            return@also
        }
        exclusiveContent {
            forRepository {
                repository
            }
            filter {
                if (includeSubgroups) {
                    @Suppress("UnstableApiUsage")
                    includeGroupAndSubgroups(group)
                } else {
                    includeGroup(group)
                }
            }
        }
    }

fun RepositoryHandler.configureGithubPagesMaven(owner: String, repo: String) {
    configureMaven(
        repo.setCase(`kebab-case`, PascalCase),
        GithubRepo.getPagesUrl(owner, repo)
    )
}
