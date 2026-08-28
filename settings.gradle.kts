pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor") version "8.0.7"
}

projekt {
    version = "8.0.8"
    license { mit() }
    gradlePlugin()
}
