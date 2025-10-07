import io.github.diskria.projektor.settings.licenses.MIT

pluginManagement {
    repositories {
        mavenCentral()
        maven(uri("../../build/localMaven"))
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "2.+"
}

projekt {
    description = "Description of test gradle plugin"
    version = "0.1.0"
    license = MIT
    versionCatalogPath = "../../gradle/libs.versions.toml"

    gradlePlugin()
}
