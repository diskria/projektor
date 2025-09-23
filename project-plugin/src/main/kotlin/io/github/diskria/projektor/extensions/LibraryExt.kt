package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.owner.domain.LibrariesDomain
import io.github.diskria.projektor.projekt.IProjekt
import io.github.diskria.projektor.projekt.Projekt
import io.github.diskria.projektor.projekt.PublishingTarget
import io.github.diskria.projektor.projekt.Versions
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun Project.configureLibrary(
    license: License = MitLicense,
    publishingTarget: PublishingTarget? = PublishingTarget.MAVEN_CENTRAL,
    jvmTarget: JvmTarget = JvmTarget.JVM_1_8,
): IProjekt {
    val library = Projekt.of(this, LibrariesDomain, license, jvmTarget).toLibrary()
    dependencies {
        testImplementation(kotlin("test"))
        testImplementation("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
    }
    tasks.named<Test>("test") {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }
    configureProjekt(library)
    configurePublishing(library, publishingTarget)
    return library
}
