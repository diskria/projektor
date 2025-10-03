package io.github.diskria.projektor.extensions.kotlin

import io.github.diskria.gradle.utils.extensions.kotlin.common.gradleError
import io.github.diskria.gradle.utils.extensions.kotlin.semver
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.DotCase
import io.github.diskria.kotlin.utils.words.KebabCase
import io.github.diskria.kotlin.utils.words.SpaceCase
import io.github.diskria.projektor.extensions.kotlin.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.owner.ProjektOwner
import io.github.diskria.projektor.projekt.Projekt
import io.github.diskria.projektor.projekt.Versions
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun Project.projekt(owner: ProjektOwner, license: License, jvmTarget: JvmTarget? = null): Projekt {
    val projectName = rootProject.name
    val javaVersion = Versions.JAVA
    return Projekt(
        owner = owner,
        license = license,
        name = projectName,
        description = rootProject.description ?: gradleError("Projekt description not set!"),
        semver = rootProject.semver(),
        slug = projectName.setCase(SpaceCase, KebabCase).lowercase(),
        packageName = owner.namespace + Constants.Char.DOT + projectName.setCase(SpaceCase, DotCase),
        javaVersion = javaVersion,
        jvmTarget = jvmTarget ?: javaVersion.toJvmTarget(),
        kotlinVersion = Versions.KOTLIN,
        project = this,
    )
}
