plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.kotlin.serialization)
}

val commonProject = project(":common")

dependencies {
    compileOnly(kotlin("gradle-plugin"))

    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.implementation.settings.plugins)

    compileOnly(commonProject)
}

tasks {
    jar {
        dependsOn(commonProject.tasks.jar)
        from(commonProject.sourceSets.map { it.output })
    }
}

group = "io.github.diskria"
version = "5.0.1"

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
        maven(layout.buildDirectory.dir("maven")) {
            name = "GithubPages"
        }
    }
}
