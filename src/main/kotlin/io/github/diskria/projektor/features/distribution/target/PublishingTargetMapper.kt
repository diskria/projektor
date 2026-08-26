package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.DistributionTargetType.*

internal fun DistributionTargetType.mapToModel(): DistributionTarget =
    when (this) {
        GITHUB_PACKAGES -> GithubPackagesDistributionTarget
        GITHUB_PAGES -> GithubPagesDistributionTarget
        MAVEN_CENTRAL -> MavenCentralDistributionTarget
        MAVEN_LOCAL -> MavenLocalDistributionTarget
        GRADLE_PLUGIN_PORTAL -> GradlePluginPortal
    }
