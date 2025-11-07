package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.extensions.mappers.toInt
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun TaskContainer.configureJvmTarget(target: JvmTarget) {
    withType<JavaCompile>().configureEach {
        options.release = target.toInt()
    }
    withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget = target
    }
}
