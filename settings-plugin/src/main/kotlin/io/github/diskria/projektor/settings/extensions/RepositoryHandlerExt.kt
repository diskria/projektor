package io.github.diskria.projektor.settings.extensions

import io.ktor.http.*
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.configureMaven(
    name: String,
    url: Url,
    group: String? = null,
    includeSubgroups: Boolean = true,
): MavenArtifactRepository =
    maven(url.toString()) {
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
