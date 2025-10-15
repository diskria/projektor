import io.github.diskria.projektor.publishing.maven.GithubPages

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

projekt {
    publishingTarget = GithubPages

    gradlePlugin {
        isSettingsPlugin = true
    }
}
