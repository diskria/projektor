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
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
}

if (true) {
    group = "io.github.diskria"
    version = "3.5.12"
    publishing {
        publications.withType<MavenPublication> {
            artifactId = "projektor-common"
        }
        repositories {
            maven(getBuildDirectory("localMaven")) {
                name = "GithubPages"
            }
        }
    }
} else {
    pluginManager.withPlugin("io.github.diskria.projektor") {
        runExtension<ProjektExtension> {
            kotlinLibrary()
        }
    }
}
