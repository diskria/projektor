package io.github.diskria.projektor.readme.shields

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.projekt.common.IProjekt
import io.ktor.http.*

open class AbstractLatestGithubShield(
    projekt: IProjekt,
    label: String,
    latestScope: String,
    url: String? = null,
) : DynamicShield(
    pathParts = listOf("github", "v", "tag", projekt.owner, projekt.repo),
    label = label,
    extraParameters = listOf(
        "sort" to "semver",
    ),
    url = url ?: buildUrl("github.com", URLProtocol.HTTPS) {
        path(projekt.owner, projekt.repo, latestScope, "latest")
    },
)
