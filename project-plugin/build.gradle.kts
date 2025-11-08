plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    implementation(libs.kotlin.html)
    implementation(libs.kotlin.serialization.xml)
    implementation(libs.jsoup)
    implementation(libs.java.poet)

    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.implementation.project.plugins)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

projekt {
    gradlePlugin()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
}
