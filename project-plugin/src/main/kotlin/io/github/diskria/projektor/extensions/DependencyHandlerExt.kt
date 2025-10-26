package io.github.diskria.projektor.extensions

import io.github.diskria.gradle.utils.extensions.add
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.minecraft(
    groupId: String,
    artefactId: String,
    version: String,
    classifier: String? = null
): Dependency? =
    add("minecraft", groupId, artefactId, version, classifier)

fun DependencyHandler.mappings(
    groupId: String,
    artefactId: String,
    version: String,
    classifier: String? = null
): Dependency? =
    add("mappings", groupId, artefactId, version, classifier)

fun DependencyHandler.modImplementation(
    groupId: String,
    artefactId: String,
    version: String,
    classifier: String? = null
): Dependency? =
    add("modImplementation", groupId, artefactId, version, classifier)

fun DependencyHandler.modImplementation(dependencyNotation: Any): Dependency? =
    add("modImplementation", dependencyNotation)
