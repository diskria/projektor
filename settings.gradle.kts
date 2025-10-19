import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PAGES
import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator

pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor")
        gradlePluginPortal()
    }

    val shouldIncludeTestProjects = true
    val isPublishingGradleTaskRunning = gradle.startParameter.taskNames.firstOrNull()?.startsWith("publish") == true
    if (shouldIncludeTestProjects && !isPublishingGradleTaskRunning && rootDir.resolve("build/localMaven").exists()) {
        includeBuild("test-gradle-plugin")
        includeBuild("test-kotlin-library")
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    version = "3.5.15"
    license = MIT
    publish = GITHUB_PAGES

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":common", ":settings-plugin", ":project-plugin")
