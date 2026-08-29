package builder.envs_generator.extensions

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

inline fun <reified T : Task> defaultTaskName(): String =
    requireNotNull(T::class.simpleName) {
        "Cannot derive task name: class '${T::class}' does not have a simple name"
    }.removeSuffix("Task").decapitalized()

inline fun <reified T : Task> TaskContainer.register(
    vararg constructorArguments: Any,
    name: String = defaultTaskName<T>(),
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> = register(name, T::class.java, *constructorArguments).apply { configure(configure) }
