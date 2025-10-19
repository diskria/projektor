import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.projektor.extensions.gradle.ProjektExtension

plugins {
    `kotlin-dsl`
    if (true) {
        `maven-publish`
    } else {
        alias(libs.plugins.projektor)
    }
}

dependencies {
    implementation(libs.foojay.resolver.plugin)
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
}

if (true) {
    group = "io.github.diskria"
    version = "3.5.12"
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
            maven(getBuildDirectory("localMaven")) {
                name = "GithubPages"
            }
        }
    }
} else {
    pluginManager.withPlugin("io.github.diskria.projektor") {
        runExtension<ProjektExtension> {
            gradlePlugin {
                isSettingsPlugin = true
            }
        }
    }
}
