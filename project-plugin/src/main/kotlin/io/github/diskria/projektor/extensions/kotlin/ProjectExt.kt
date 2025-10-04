package io.github.diskria.projektor.extensions.kotlin

import io.github.diskria.gradle.utils.extensions.kotlin.common.gradleError
import io.github.diskria.gradle.utils.extensions.kotlin.semver
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.properties.AutoNamedEnvironmentVariable
import io.github.diskria.projektor.extensions.kotlin.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.Projekt
import io.github.diskria.projektor.projekt.Versions
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun Project.toProjekt(license: License, jvmTarget: JvmTarget? = null): Projekt {
    val (owner, repo) = if (providers.environmentVariable("CI").isPresent) {
        val githubOwner = providers.environmentVariable("GITHUB_OWNER").orNull ?: gradleError(
            "Environment variable GITHUB_OWNER must be set"
        )
        val githubRepo = providers.environmentVariable("GITHUB_REPO").orNull ?: gradleError(
            "Environment variable GITHUB_REPO must be set"
        )
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
