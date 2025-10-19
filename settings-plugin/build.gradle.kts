import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.projektor.extensions.gradle.ProjektExtension
import io.github.diskria.projektor.extensions.java
import io.github.diskria.projektor.extensions.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    version = "3.5.14"
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
            implementation.set(JvmImplementation.VENDOR_SPECIFIC)
            vendor.set(JvmVendorSpec.ADOPTIUM)
        }
        withSourcesJar()
        withJavadocJar()
    }
    kotlin {
        jvmToolchain(25)
    }
    tasks.withType<JavaCompile>().configureEach {
        with(options) {
            release.set(25)
            encoding = Charsets.UTF_8.toString()
        }
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_24)
        }
    }
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
