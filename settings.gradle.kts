import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PAGES
import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator

pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor")
        gradlePluginPortal()
    }

    val shouldIncludeTestProjects = true
    val runningTaskName = gradle.startParameter.taskNames.firstOrNull()
    val isPublishTaskRunning = runningTaskName?.startsWith("publish") == true
    if (shouldIncludeTestProjects && !isPublishTaskRunning && rootDir.resolve("build/maven/github-pages").exists()) {
        rootDir.resolve("test").listFiles()?.forEach { includeBuild(it) }
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    version = "3.6.6"
    license = MIT
    publish = setOf(GITHUB_PAGES)

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":common", ":settings-plugin", ":project-plugin")
