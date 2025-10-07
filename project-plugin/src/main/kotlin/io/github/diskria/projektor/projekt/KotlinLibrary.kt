package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.testImplementation
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate

class KotlinLibrary(projekt: IProjekt, val project: Project) : AbstractProjekt(projekt), IProjekt by projekt {

    override fun configureProject() = with(project) {
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
    }

    override fun getMetadata(): List<Property<String>> {
        val libraryName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(libraryName)
    }
}
