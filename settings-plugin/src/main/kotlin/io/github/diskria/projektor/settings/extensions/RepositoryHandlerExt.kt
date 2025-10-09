package io.github.diskria.projektor.settings.extensions

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
