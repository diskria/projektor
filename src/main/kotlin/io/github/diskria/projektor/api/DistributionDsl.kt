package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.DistributionTargetType
import org.gradle.api.provider.ListProperty

class DistributionDsl internal constructor(private val targets: ListProperty<DistributionTargetType>) : ProjektorScope {
    fun mavenLocal() = targets.add(DistributionTargetType.MAVEN_LOCAL)
    fun mavenCentral() = targets.add(DistributionTargetType.MAVEN_CENTRAL)
    fun githubPages() = targets.add(DistributionTargetType.GITHUB_PAGES)
    fun githubPackages() = targets.add(DistributionTargetType.GITHUB_PACKAGES)
    fun gradlePluginPortal() = targets.add(DistributionTargetType.GRADLE_PLUGIN_PORTAL)
}
