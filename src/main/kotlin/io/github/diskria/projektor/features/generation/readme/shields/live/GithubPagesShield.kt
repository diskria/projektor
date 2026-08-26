package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.DistributionTargetType.GITHUB_PAGES
import io.github.diskria.projektor.core.model.Projekt

internal class GithubPagesShield(projekt: Projekt) : GithubLatestTagShield(GITHUB_PAGES, projekt)
