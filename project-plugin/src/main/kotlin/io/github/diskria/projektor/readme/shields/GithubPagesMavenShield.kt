package io.github.diskria.projektor.readme.shields

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.projekt.common.IProjekt
import io.ktor.http.*

class GithubPagesMavenShield(projekt: IProjekt) : LatestGithubReleaseShield(
    projekt = projekt,
    label = "GitHub Pages Maven",
    url = buildUrl("${projekt.owner}.github.io", URLProtocol.HTTPS) {
        path(projekt.repo)
    }
)
