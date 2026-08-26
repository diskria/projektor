package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.DistributionTargetType.*

internal fun DistributionTargetType.mapToModel(): DistributionTarget =
    when (this) {
        MAVEN_LOCAL -> MavenLocalDistributionTarget
        MAVEN_CENTRAL -> MavenCentralDistributionTarget
        GITHUB_PAGES -> GithubPagesDistributionTarget
        GITHUB_PACKAGES -> GithubPackagesDistributionTarget
        GRADLE_PLUGIN_PORTAL -> GradlePluginPortalDistributionTarget
    }
