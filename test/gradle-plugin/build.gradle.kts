import io.github.diskria.projektor.publishing.MavenCentral

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

projekt {

    gradlePlugin()
}
