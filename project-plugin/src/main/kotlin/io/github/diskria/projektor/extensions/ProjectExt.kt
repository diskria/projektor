package io.github.diskria.projektor.extensions

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.gradle.utils.extensions.semver
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.properties.AutoNamedEnvironmentVariable
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.common.Projekt
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun Project.toProjekt(license: License, jvmTarget: JvmTarget? = null): Projekt {
    val (owner, repo) = if (providers.isCI) {
        val githubOwner by AutoNamedEnvironmentVariable(isRequired = true)
        val githubRepo by AutoNamedEnvironmentVariable(isRequired = true)
        githubOwner to githubRepo
    } else {
        rootDir.parentFile.asDirectory().name to rootDir.name
    }
    return Projekt(
        owner = owner,
        license = license,
        repo = repo,
        description = rootProject.description ?: gradleError("Projekt description not set!"),
        semver = rootProject.semver(),
        email = "diskria@proton.me",
        javaVersion = Versions.JAVA,
        jvmTarget = jvmTarget ?: Versions.JAVA.toJvmTarget(),
        kotlinVersion = Versions.KOTLIN,
    )
}
