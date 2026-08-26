package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.DistributionTargetType.GITHUB_PACKAGES
import io.github.diskria.projektor.core.model.Projekt

internal class GithubPackagesShield(projekt: Projekt) : GithubLatestTagShield(GITHUB_PACKAGES, projekt)
