package io.github.diskria.projektor.readme.shields

import io.github.diskria.projektor.projekt.common.IProjekt

class LatestGithubPackageShield(projekt: IProjekt) : AbstractLatestGithubShield(
    projekt = projekt,
    label = "GitHub Package",
    latestScope = "packages",
)
