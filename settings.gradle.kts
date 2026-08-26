pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor/")
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor") version "8.0.5"
}

projekt {
    version = "8.0.6"
    licensing { mit() }
    gradlePlugin()
}
