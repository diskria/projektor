import io.github.diskria.projektor.gradle.extensions.configureGradlePlugin
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.projekt.PublishingTarget

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
}

dependencies {
    compileOnly(project(":common"))

    compileOnly(libs.kotlin.utils)
}

configureGradlePlugin(GithubProfile, PublishingTarget.GITHUB_PAGES, isSettingsPlugin = true)
