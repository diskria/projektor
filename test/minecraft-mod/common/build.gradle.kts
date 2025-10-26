import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.projektor) version "4.+"
}

projekt {
    kotlinLibrary {
        jvmTarget = JvmTarget.JVM_17
    }
}
