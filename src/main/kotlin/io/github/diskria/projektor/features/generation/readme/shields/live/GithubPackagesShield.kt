package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.PublishingTargetType.GITHUB_PACKAGES

internal class GithubPackagesShield(projekt: Projekt) : GithubLatestTagShield(GITHUB_PACKAGES, projekt)
