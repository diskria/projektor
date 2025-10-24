import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.common.publishing.PublishingTargetType.MODRINTH

pluginManagement {
    repositories {
        val mavenDirectory = rootDir.parentFile.parentFile.resolve("build/maven")
        maven(uri(mavenDirectory.listFiles().orEmpty().min()))
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    version = "0.1.0"
    license = MIT
    publish = setOf(MODRINTH)

    minecraftMod()
}
