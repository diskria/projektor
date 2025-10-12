import io.github.diskria.projektor.common.licenses.MIT

pluginManagement {
    repositories {
        maven("https://repo1.maven.org/maven2")
        mavenCentral()
        maven(uri("../../build/localMaven"))
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
