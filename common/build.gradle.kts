import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(gradleKotlinDsl())
    compileOnly(libs.bundles.diskria.utils)
    compileOnly(libs.bundles.ktor.client)
    compileOnly(libs.kotlin.serialization.xml)
    compileOnly(libs.jsoup)
}

projekt {
    kotlinLibrary {
        jvmTarget = JvmTarget.JVM_21
    }
}
