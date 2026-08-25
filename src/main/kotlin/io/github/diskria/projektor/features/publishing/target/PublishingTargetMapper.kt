package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.PublishingTargetType
import io.github.diskria.projektor.core.model.PublishingTargetType.*

internal fun PublishingTargetType.mapToModel(): PublishingTarget =
    when (this) {
        GITHUB_PACKAGES -> GithubPackages
        GITHUB_PAGES -> GithubPages
        MAVEN_CENTRAL -> MavenCentral
        MAVEN_LOCAL -> MavenLocal
        GRADLE_PLUGIN_PORTAL -> GradlePluginPortal
    }
