import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.projektor.extensions.kotlin.publishing

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

dependencies {
    implementation(libs.kotlin.utils)

    implementation(libs.gradle.utils)
}

group = "io.github.diskria"
version = "2.0.1"

gradlePlugin {
    plugins {
        create("myPlugin") {
            id = "io.github.diskria.projektor.settings"
            implementationClass = "io.github.diskria.projektor.settings.ProjektorSettingsGradlePlugin"
        }
    }
}

publishing {
    repositories {
        maven(getBuildDirectory("repo")) {
            name = "GitHubPages"
        }
    }
}
