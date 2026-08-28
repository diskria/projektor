pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor") version "8.0.6"
}

projekt {
    version = "8.0.8"
    licensing { mit() }
    gradlePlugin()
}
