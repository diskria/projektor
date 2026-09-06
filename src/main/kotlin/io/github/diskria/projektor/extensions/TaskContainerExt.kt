package io.github.diskria.projektor.extensions

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

@PublishedApi
internal inline fun <reified T : Task> defaultTaskName(): String =
    defaultNameBySuffix<T>("Task")

inline fun <reified T : Task> TaskContainer.register(
    vararg constructorArgs: Any,
    name: String = defaultTaskName<T>(),
    noinline configure: (T) -> Unit = {}
): TaskProvider<T> = register(name, T::class.java, *constructorArgs).apply { configure(configure) }

inline fun <reified T : Task> TaskContainer.isRegistered(): Boolean =
    names.contains(defaultTaskName<T>())

inline fun <reified T : Task> TaskContainer.findByType(): T? =
    findByName(defaultTaskName<T>()) as? T

inline fun <reified T : Task> TaskContainer.getByType(): T =
    getByName(defaultTaskName<T>()) as T
