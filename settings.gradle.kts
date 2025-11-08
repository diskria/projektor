import io.github.diskria.kotlin.utils.extensions.asDirectoryOrNull
import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PACKAGES
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PAGES

pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor") {
            name = "Projektor"
        }
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "4.+"
}

projekt {
    version = "4.6.1"
    license = MIT
    publish = setOf(
        GITHUB_PAGES,
        GITHUB_PACKAGES,
    )

    gradlePlugin()
}

include(":common")
include(":settings-plugin", ":project-plugin")

if (rootDir.resolve("build/maven").listFiles().orEmpty().isNotEmpty()) {
    val taskName = gradle.startParameter.taskNames.singleOrNull()
    val testProjectsRoot = rootDir.resolve("test")
    if (taskName?.startsWith(":") == true) {
        val testProjectName = taskName.split(":").first { it.isNotBlank() }
        testProjectsRoot.resolve(testProjectName).asDirectoryOrNull()?.let { includeBuild(it) }
    } else if (taskName != "releaseProjekt") {
        testProjectsRoot.listDirectories().forEach { includeBuild(it) }
    }
}
