import io.github.diskria.projektor.publishing.LocalMaven

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

projekt {
    publishingTarget = LocalMaven

    gradlePlugin()
}
