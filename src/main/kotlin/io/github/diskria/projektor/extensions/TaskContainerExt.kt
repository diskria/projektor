package io.github.diskria.projektor.extensions

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@PublishedApi
internal inline fun <reified T : Task> defaultTaskName(): String =
    checkNotNull(T::class.simpleName) {
        "Cannot derive task name: class '${T::class}' does not have a simple name"
    }.removeSuffix("Task").decapitalized()

internal fun TaskContainer.jar(configure: Jar.() -> Unit): TaskProvider<Jar> =
    named<Jar>("jar", configure)

internal fun TaskContainer.configureJvmTarget(target: JvmTarget) {
    withType<KotlinCompile>().configureEach { it.compilerOptions.jvmTarget.set(target) }
    withType<JavaCompile>().configureEach { it.options.release.set(target.toVersion()) }
}

internal inline fun <reified T : Task> TaskContainer.register(
    vararg constructorArguments: Any,
    name: String = defaultTaskName<T>(),
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> = register(name, T::class.java, *constructorArguments).apply { configure(configure) }

internal inline fun <reified T : Task> TaskContainer.has(name: String = defaultTaskName<T>()): Boolean =
    findByName(name) is T

internal inline fun <reified T : Task> TaskContainer.get(name: String = defaultTaskName<T>()): TaskProvider<T> =
    named<T>(name)

internal inline fun <reified T : Task> TaskContainer.find(name: String = defaultTaskName<T>()): TaskProvider<T>? =
    if (!has<T>(name)) null
    else get<T>(name)
