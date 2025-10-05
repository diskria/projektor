import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.publishing.GitHubPages

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

if (findProperty("dogfooding").toString().toBoolean()) {
    projekt {
        license = MitLicense
        publishingTarget = GitHubPages

        gradlePlugin {
            isSettingsPlugin = true
            tags = setOf("settings", "configuration")
        }
    }
} else {
    group = "io.github.diskria"
    version = "2.2.0"

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
}
