import io.github.diskria.projektor.extensions.publishing
import org.gradle.internal.impldep.it.unimi.dsi.fastutil.longs.LongLists.emptyList
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.collections.emptyList

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
    alias(libs.plugins.kotlin.serialization)
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
    gradlePlugin {
        jvmTarget = JvmTarget.JVM_21
    }
}
