import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PACKAGES
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PAGES
import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator

pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor")
        gradlePluginPortal()
    }

    if (rootDir.resolve("build/maven").listFiles().orEmpty().isNotEmpty()) {
        val task = gradle.startParameter.taskNames.singleOrNull()
        val testProjectsRoot = rootDir.resolve("test")
        if (task?.startsWith(":") == true) {
            val testProjectDirectory = testProjectsRoot.resolve(task.split(":").first { it.isNotBlank() })
            if (testProjectDirectory.exists()) {
                includeBuild(testProjectDirectory)
            }
        } else if (task != "releaseProjekt") {
            testProjectsRoot.listFiles { it.isDirectory && !it.isHidden }?.forEach { includeBuild(it) }
        }
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "4.+"
}

projekt {
    version = "4.2.3"
    license = MIT
    publish = setOf(
        GITHUB_PAGES,
        GITHUB_PACKAGES,
    )

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":common", ":settings-plugin", ":project-plugin")
