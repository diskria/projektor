import io.github.diskria.projektor.common.licenses.MIT
import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator

pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor")
        gradlePluginPortal()
    }

    fun File.forEachDirectory(action: (File) -> Unit) {
        listFiles()?.filter { it.isDirectory && !it.isHidden }.orEmpty().forEach(action)
    }

    val shouldIncludeTestProjects = true
    val isPublishingGradleTaskRunning = gradle.startParameter.taskNames.firstOrNull()?.startsWith("publish") == true
    if (shouldIncludeTestProjects && !isPublishingGradleTaskRunning && rootDir.resolve("build/localMaven").exists()) {
        rootDir.resolve("test-projects").forEachDirectory { publishingTargetDirectory ->
            publishingTargetDirectory.forEachDirectory { testProjectDirectory ->
                includeBuild(publishingTargetDirectory.resolve(testProjectDirectory.name))
            }
        }
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "3.2.22"
    license = MIT
    tags = setOf("configuration")

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":common", ":settings-plugin", ":project-plugin")
