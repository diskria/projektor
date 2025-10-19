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
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.html)

    compileOnly(kotlin("gradle-plugin"))
    compileOnly(libs.fabric.loom.plugin)
    implementation(libs.build.config.plugin)
    implementation(libs.kotlin.jvm.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.neoforge.moddev.plugin)
    implementation(libs.modrinth.minotaur.plugin)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

if (true) {
    group = "io.github.diskria"
    version = "3.5.13"
    gradlePlugin {
        plugins {
            create("io.github.diskria.projektor") {
                id = "io.github.diskria.projektor"
                implementationClass = "io.github.diskria.projektor.ProjektorGradlePlugin"
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
            gradlePlugin()
        }
    }
}
