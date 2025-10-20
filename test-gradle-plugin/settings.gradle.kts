import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GRADLE_PLUGIN_PORTAL

pluginManagement {
    repositories {
        maven(uri("../build/localMaven"))
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    version = "0.1.0"
    license = MIT
    publish = GRADLE_PLUGIN_PORTAL

    gradlePlugin {
        versionCatalogPath = "../gradle/libs.versions.toml"
    }
}

include(":moduleA", ":moduleB")
