package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.PublishingTargetType
import org.gradle.api.provider.ListProperty

class PublishingDsl internal constructor(
    private val publishingTargets: ListProperty<PublishingTargetType>
) : ProjektorScope {

    fun githubPages() {
        publishingTargets.add(PublishingTargetType.GITHUB_PAGES)
    }

    fun githubPackages() {
        publishingTargets.add(PublishingTargetType.GITHUB_PACKAGES)
    }

    fun mavenCentral() {
        publishingTargets.add(PublishingTargetType.MAVEN_CENTRAL)
    }

    fun mavenLocal() {
        publishingTargets.add(PublishingTargetType.MAVEN_LOCAL)
    }

    fun gradlePluginPortal() {
        publishingTargets.add(PublishingTargetType.GRADLE_PLUGIN_PORTAL)
    }
}
