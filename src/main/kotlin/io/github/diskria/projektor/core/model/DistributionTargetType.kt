package io.github.diskria.projektor.core.model

internal enum class DistributionTargetType(val id: String, val displayName: String) {
    GITHUB_PAGES("github-pages", "GitHub Pages"),
    GITHUB_PACKAGES("github-packages", "GitHub Packages"),
    MAVEN_CENTRAL("maven-central", "Maven Central"),
    MAVEN_LOCAL("maven-local", "Maven Local"),
    GRADLE_PLUGIN_PORTAL("gradle-plugin-portal", "Gradle Plugin Portal"),
}
