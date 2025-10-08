package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.testImplementation
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.named

open class KotlinLibraryConfigurator(
    val config: KotlinLibraryConfiguration
) : Configurator<KotlinLibrary>() {

    override fun configure(project: Project, projekt: IProjekt): KotlinLibrary = with(project) {
        val kotlinLibrary = KotlinLibrary(projekt, config)
        applyCommonConfiguration(project, kotlinLibrary)
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
        return kotlinLibrary
    }
}
