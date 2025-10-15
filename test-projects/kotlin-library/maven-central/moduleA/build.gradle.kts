import io.github.diskria.projektor.publishing.maven.MavenCentral

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

projekt {
    publishingTarget = MavenCentral

    kotlinLibrary()
}
