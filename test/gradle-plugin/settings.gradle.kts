pluginManagement {
    includeBuild("../../settings-plugin") {
        name = "settings-plugin-dogfooding"
    }
    includeBuild("../../project-plugin") {
        name = "project-plugin-dogfooding"
    }
}

plugins {
    id("io.github.diskria.projektor.settings")
}

projekt {
    name = "Test Gradle Plugin"
    description = "Description of test gradle plugin"
    version = "0.1.0"

    gradlePlugin()
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}
