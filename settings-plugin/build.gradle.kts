import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory

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

//projekt {
//    license = MitLicense
//    publishingTarget = GitHubPages
//
//    gradlePlugin {
//        isSettingsPlugin = true
//        tags = setOf("project", "configuration")
//    }
//}

group = "io.github.diskria"
version = "2.1.0"

gradlePlugin {
    plugins {
        create("io.github.diskria.projektor.settings") {
            id = "io.github.diskria.projektor.settings"
            implementationClass = "io.github.diskria.projektor.settings.ProjektorGradlePlugin"
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
