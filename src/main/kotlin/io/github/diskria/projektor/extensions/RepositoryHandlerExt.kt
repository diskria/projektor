package io.github.diskria.projektor.extensions

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

internal fun RepositoryHandler.mavenCentralWithDirect() {
    mavenCentral { repo -> repo.name = "MavenCentral" }
    maven("https://repo1.maven.org/maven2") { name = "MavenCentralDirect" }
}
