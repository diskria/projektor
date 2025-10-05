import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.publishing.GitHubPages

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(kotlin("gradle-plugin"))

    compileOnly(libs.build.config.plugin)
    compileOnly(libs.fabric.plugin)
    compileOnly(libs.neoforge.plugin)
    compileOnly(libs.modrinth.plugin)

    implementation(libs.ktor.http)
    implementation(libs.kotlin.utils)
    implementation(libs.kotlin.serialization)

    implementation(libs.gradle.utils)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

if (findProperty("dogfooding").toString().toBoolean()) {
    projekt {
        license = MitLicense
        publishingTarget = GitHubPages

        gradlePlugin {
            tags = setOf("project", "configuration")
        }
    }
} else {
    group = "io.github.diskria"
    version = "2.1.1"

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
            maven(getBuildDirectory("repo")) {
                name = "GitHubPages"
            }
        }
    }
}
