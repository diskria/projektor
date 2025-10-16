package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.common.publishing.PublishingTargetType.*
import io.github.diskria.projektor.publishing.common.PublishingTarget
import io.github.diskria.projektor.publishing.external.GooglePlay
import io.github.diskria.projektor.publishing.external.GradlePluginPortal
import io.github.diskria.projektor.publishing.external.Modrinth
import io.github.diskria.projektor.publishing.maven.GithubPackages
import io.github.diskria.projektor.publishing.maven.GithubPages
import io.github.diskria.projektor.publishing.maven.MavenCentral

fun PublishingTargetType.mapToModel(): PublishingTarget =
    when (this) {
        GITHUB_PACKAGES -> GithubPackages
        GITHUB_PAGES -> GithubPages
        MAVEN_CENTRAL -> MavenCentral
        GRADLE_PLUGIN_PORTAL -> GradlePluginPortal
        MODRINTH -> Modrinth
        GOOGLE_PLAY -> GooglePlay
    }

fun PublishingTarget.mapToEnum(): PublishingTargetType =
    when (this) {
        GithubPackages -> GITHUB_PACKAGES
        GithubPages -> GITHUB_PAGES
        MavenCentral -> MAVEN_CENTRAL
        GradlePluginPortal -> GRADLE_PLUGIN_PORTAL
        Modrinth -> MODRINTH
        GooglePlay -> GOOGLE_PLAY
        else -> failWithUnsupportedType(this::class)
    }
