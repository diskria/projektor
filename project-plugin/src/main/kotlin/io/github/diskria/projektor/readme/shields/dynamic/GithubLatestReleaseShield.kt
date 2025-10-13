package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.projekt.common.IProjekt
import io.ktor.http.*

open class GithubLatestReleaseShield(projekt: IProjekt) : DynamicShield(
    pathParts = listOf("github", "v", "tag", projekt.owner, projekt.repo),
    extraParameters = listOf(
        "sort" to "semver",
    ),
    label = "Latest Release",
    url = buildUrl("github.com", URLProtocol.HTTPS) {
        path(projekt.owner, projekt.repo, "releases", "latest")
    },
)
