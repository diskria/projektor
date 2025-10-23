import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType

pluginManagement {
    repositories {
        maven(uri("../../build/maven/github-pages"))
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    version = "0.1.0"
    license = MIT
    publish = setOf(PublishingTargetType.MAVEN_CENTRAL)

    kotlinLibrary()
}

include(":moduleA", ":moduleB")
