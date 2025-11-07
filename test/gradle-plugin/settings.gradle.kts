import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PACKAGES
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PAGES

pluginManagement {
    repositories {
        val mavenDirectory = rootDir.parentFile.parentFile.resolve("build/maven")
        maven(uri(mavenDirectory.listFiles().orEmpty().min())) {
            name = "Projektor"
        }
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "4.+"
}

projekt {
    version = "0.1.0"
    license = MIT
    publish = setOf(
        GITHUB_PAGES,
        GITHUB_PACKAGES,
    )

    gradlePlugin()
}

include(":moduleA", ":moduleB")
