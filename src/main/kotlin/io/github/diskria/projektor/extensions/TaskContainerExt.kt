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

internal fun TaskContainer.jar(configure: Jar.() -> Unit): TaskProvider<Jar> =
    named<Jar>("jar", configure)

internal fun TaskContainer.configureJvmTarget(target: JvmTarget) {
    withType<KotlinCompile>().configureEach { kotlinCompile ->
        kotlinCompile.compilerOptions.jvmTarget.set(target)
    }
    withType<JavaCompile>().configureEach { javaCompile ->
        javaCompile.options.release.set(target.toVersion())
    }
}

@PublishedApi
internal inline fun <reified T : Task> defaultTaskName(): String =
    defaultNameBySuffix<T>("Task")

internal inline fun <reified T : Task> TaskContainer.register(
    vararg constructorArgs: Any,
    name: String = defaultTaskName<T>(),
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> = register(name, T::class.java, *constructorArgs).apply { configure(configure) }

@PublishedApi
internal inline fun <reified T : Task> TaskContainer.isRegistered(): Boolean =
    names.contains(defaultTaskName<T>())

internal inline fun <reified T : Task> TaskContainer.findByType(): T? =
    findByName(defaultTaskName<T>()) as? T

internal inline fun <reified T : Task> TaskContainer.getByType(): T =
    getByName(defaultTaskName<T>()) as T
