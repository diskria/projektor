package io.github.diskria.projektor.markdown.shields

import io.github.diskria.projektor.projekt.common.IProjekt

open class LatestGithubReleaseShield(
    projekt: IProjekt,
    label: String = "Latest Release",
    url: String? = null,
) : AbstractGithubLatestShield(
    projekt = projekt,
    label = label,
    latestScope = "releases",
    url,
)
