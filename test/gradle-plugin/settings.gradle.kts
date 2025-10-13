import io.github.diskria.projektor.common.licenses.MIT

pluginManagement {
    repositories {
        maven(uri("../../build/localMaven"))
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    description = "Description of test gradle plugin."
    version = "0.1.0"
    license = MIT
    tags = setOf("test")
    versionCatalogPath = "../../gradle/libs.versions.toml"

    gradlePlugin()
}
