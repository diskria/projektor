package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PAGES

class GithubPagesShield(metadata: ProjektMetadata) : GithubLatestTagShield(GITHUB_PAGES, metadata)
