package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.PublishingTargetType.GITHUB_PAGES

internal class GithubPagesShield(projekt: Projekt) : GithubLatestTagShield(GITHUB_PAGES, projekt)
