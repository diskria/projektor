import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.projektor)
}

projekt {
    kotlinLibrary {
        jvmTarget = JvmTarget.JVM_17
    }
}
