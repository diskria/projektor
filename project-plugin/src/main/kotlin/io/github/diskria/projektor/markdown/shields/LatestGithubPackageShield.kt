package io.github.diskria.projektor.markdown.shields

import io.github.diskria.projektor.projekt.common.IProjekt

class LatestGithubPackageShield(projekt: IProjekt) : AbstractGithubLatestShield(
    projekt = projekt,
    label = "GitHub Package",
    latestScope = "packages",
)
