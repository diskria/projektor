package builder.metadata_generator.extensions

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

inline fun <reified T : Task> defaultTaskName(): String =
    defaultNameBySuffix<T>("Task")

inline fun <reified T : Task> TaskContainer.register(
    vararg constructorArgs: Any,
    name: String = defaultTaskName<T>(),
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> = register(name, T::class.java, *constructorArgs).apply { configure(configure) }

inline fun <reified T : Task> TaskContainer.getByType(): T =
    getByName(defaultTaskName<T>()) as T
