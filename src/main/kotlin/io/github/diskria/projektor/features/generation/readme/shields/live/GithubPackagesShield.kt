package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.PublishingTargetType.GITHUB_PACKAGES
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata

internal class GithubPackagesShield(metadata: ProjektMetadata) : GithubLatestTagShield(GITHUB_PACKAGES, metadata)
