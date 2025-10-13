package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.projekt.common.IProjekt
import io.ktor.http.*

class GithubPagesShield(projekt: IProjekt) : DynamicShield(
    pathParts = listOf("github", "v", "tag", projekt.owner, projekt.repo),
    extraParameters = listOf(
        "sort" to "semver",
    ),
    label = "GitHub Pages",
    url = buildUrl("${projekt.owner}.github.io", URLProtocol.HTTPS) {
        path(projekt.repo)
    }
)
