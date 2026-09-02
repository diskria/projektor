package io.github.diskria.projektor.extensions

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.mavenCentralWithDirect() {
    mavenCentral { it.name = "MavenCentral" }
    maven("https://repo1.maven.org/maven2") { name = "MavenCentralDirect" }
}
