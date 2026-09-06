package io.github.diskria.projektor.extensions

import org.gradle.api.artifacts.dsl.RepositoryHandler

internal fun RepositoryHandler.mavenCentralWithDirect() {
    mavenCentral { repository ->
        repository.name = "MavenCentral"
    }
    maven { repository ->
        repository.name = "MavenCentralDirect"
        repository.setUrl("https://repo1.maven.org/maven2")
    }
}
