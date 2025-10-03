import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
//import io.github.diskria.projektor.settings.extensions.kotlin.configureMaven
//import io.github.diskria.projektor.settings.extensions.kotlin.publishing

plugins {
    `kotlin-dsl`
    `maven-publish`
//    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

dependencies {
    implementation(libs.kotlin.utils)

    implementation(libs.gradle.utils)
}

group = "io.github.diskria"
version = "2.0.3"

gradlePlugin {
    plugins {
        create("io.github.diskria.projektor.settings") {
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
