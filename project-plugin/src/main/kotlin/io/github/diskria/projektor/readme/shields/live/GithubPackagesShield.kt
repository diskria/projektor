package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PACKAGES

class GithubPackagesShield(metadata: ProjektMetadata) : GithubLatestTagShield(GITHUB_PACKAGES, metadata)
