import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PACKAGES
import io.github.diskria.projektor.common.publishing.PublishingTargetType.GITHUB_PAGES
import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator

pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor")
        gradlePluginPortal()
    }

    if (!gradle.startParameter.taskNames.contains("release") &&
        rootDir.resolve("build/maven").listFiles().orEmpty().isNotEmpty()
    ) {
        rootDir.resolve("test").listFiles().orEmpty().forEach { includeBuild(it) }
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    version = "3.6.12"
    license = MIT
    publish = setOf(
        GITHUB_PAGES,
        GITHUB_PACKAGES,
    )

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":common", ":settings-plugin", ":project-plugin")
