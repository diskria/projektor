package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.PublishingTargetType.GITHUB_PAGES
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata

internal class GithubPagesShield(metadata: ProjektMetadata) : GithubLatestTagShield(GITHUB_PAGES, metadata)
