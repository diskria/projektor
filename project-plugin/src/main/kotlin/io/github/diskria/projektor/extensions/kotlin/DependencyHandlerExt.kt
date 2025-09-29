package io.github.diskria.projektor.extensions.kotlin

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.minecraft(dependencyNotation: Any): Dependency? =
    add("minecraft", dependencyNotation)

fun DependencyHandler.mappings(dependencyNotation: Any): Dependency? =
    add("mappings", dependencyNotation)

fun DependencyHandler.modImplementation(dependencyNotation: Any): Dependency? =
    add("modImplementation", dependencyNotation)
