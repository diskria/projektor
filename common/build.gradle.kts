plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)

    compileOnly(libs.bundles.diskria.utils)
    compileOnly(libs.bundles.ktor.client)
}
