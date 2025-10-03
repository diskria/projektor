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
    versionCatalog = files("../../gradle/libs.versions.toml")

    gradlePlugin()
}
