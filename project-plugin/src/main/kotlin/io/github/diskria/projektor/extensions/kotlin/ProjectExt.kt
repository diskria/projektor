package io.github.diskria.projektor.extensions.kotlin

import io.github.diskria.gradle.utils.extensions.kotlin.common.gradleError
import io.github.diskria.gradle.utils.extensions.kotlin.semver
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.projektor.extensions.kotlin.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.Projekt
import io.github.diskria.projektor.projekt.Versions
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun Project.toProjekt(license: License, jvmTarget: JvmTarget? = null): Projekt =
    Projekt(
        owner = project.findProperty("githubOwner")?.toString() ?: rootDir.parentFile.asDirectory().name,
        license = license,
        name = project.findProperty("githubRepo")?.toString() ?: rootDir.name,
        description = rootProject.description ?: gradleError("Projekt description not set!"),
        semver = rootProject.semver(),
        email = "diskria@proton.me",
        javaVersion = Versions.JAVA,
        jvmTarget = jvmTarget ?: Versions.JAVA.toJvmTarget(),
        kotlinVersion = Versions.KOTLIN,
    )
