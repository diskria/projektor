package io.github.diskria.projektor.core.model

internal enum class PublishingTargetType(val id: String, val shieldLabel: String) {
    GITHUB_PAGES("github-pages", "GitHub Pages"),
    GITHUB_PACKAGES("github-packages", "GitHub Packages"),
    MAVEN_CENTRAL("maven-central", "Maven Central"),
    GRADLE_PLUGIN_PORTAL("gradle-plugin-portal", "Gradle Plugin Portal"),
}
