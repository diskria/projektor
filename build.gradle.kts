import io.github.diskria.gradle.utils.extensions.getCatalogVersion

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

val kotlinVersion = getCatalogVersion("kotlin")
configurations.all {
    resolutionStrategy {
        eachDependency {
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
            }
        }
    }
}
