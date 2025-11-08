plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.implementation.settings.plugins)
    implementation(libs.jsoup)
}

projekt {
    gradlePlugin {
        isSettingsPlugin = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
}
