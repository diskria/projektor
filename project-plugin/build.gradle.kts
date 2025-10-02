import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.projektor.extensions.kotlin.configureMaven
import io.github.diskria.projektor.extensions.kotlin.publishing

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    configureMaven(
        name = "Minecraft",
        url = "https://libraries.minecraft.net",
        group = "com.mojang"
    )
    configureMaven(
        name = "SpongePowered",
        url = "https://repo.spongepowered.org/repository/maven-public",
        group = "org.spongepowered"
    )
    configureMaven(
        name = "Modrinth",
        url = "https://api.modrinth.com/maven",
        group = "maven.modrinth",
        includeSubgroups = false
    )
    maven("https://maven.fabricmc.net")
    mavenCentral()
    gradlePluginPortal()
    google()
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

group = "io.github.diskria"
version = "2.0.1"

gradlePlugin {
    plugins {
        create("myPlugin") {
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
