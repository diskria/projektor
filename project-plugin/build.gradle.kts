plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)

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

projekt {
    gradlePlugin()
}
