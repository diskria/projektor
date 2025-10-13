package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.projekt.common.IProjekt
import io.ktor.http.*

class GithubPackageShield(projekt: IProjekt) : DynamicShield(
    pathParts = listOf("github", "v", "tag", projekt.owner, projekt.repo),
    extraParameters = listOf(
        "sort" to "semver",
    ),
    label = "GitHub Package",
    url = buildUrl("github.com", URLProtocol.HTTPS) {
        path(projekt.owner, projekt.repo, "packages", "latest")
    },
)
