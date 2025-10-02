pluginManagement {
    repositories {
        mavenCentral()
        maven("https://diskria.github.io/projektor")
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "2.+"
}

projekt {
    name = "Projektor"
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "2.0.1"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
