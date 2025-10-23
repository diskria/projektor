import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType

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
    publish = setOf(PublishingTargetType.GITHUB_PAGES)

    gradlePlugin()
}

include(":moduleA", ":moduleB")
