package builder.metadata_generator.extensions

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

inline fun <reified T : Task> defaultTaskName(): String =
    checkNotNull(T::class.simpleName) {
        "Cannot derive task name: class '${T::class}' does not have a simple name"
    }.removeSuffix("Task").decapitalized()

inline fun <reified T : Task> TaskContainer.register(
    vararg constructorArgs: Any,
    name: String = defaultTaskName<T>(),
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> = register(name, T::class.java, *constructorArgs).apply { configure(configure) }
