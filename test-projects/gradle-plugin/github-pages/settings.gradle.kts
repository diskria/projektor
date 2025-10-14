import io.github.diskria.projektor.common.licenses.MIT

pluginManagement {
    repositories {
        maven(uri("../../../build/localMaven"))
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    description = "Test Gradle plugin for GitHub Pages publication."
    version = "0.1.0"
    license = MIT
    tags = setOf("sample")
    versionCatalogPath = "../../../gradle/libs.versions.toml"

    gradlePlugin()
}
