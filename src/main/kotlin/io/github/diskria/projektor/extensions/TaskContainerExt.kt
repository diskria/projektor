package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.core.configurators.toVersion
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.check
import io.github.diskria.projektor.internal.utils.decapitalized
import io.github.diskria.projektor.internal.utils.requireNotNull
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
    Errors.internal.requireNotNull(T::class.simpleName) {
        "Cannot derive task name: class '${T::class}' does not have a simple name"
    }.removeSuffix("Task").decapitalized()

internal fun TaskContainer.jar(configure: Jar.() -> Unit): TaskProvider<Jar> =
    named<Jar>("jar", configure)

internal fun TaskContainer.configureJvmTarget(target: JvmTarget) {
    withType<KotlinCompile>().configureEach { it.compilerOptions.jvmTarget.set(target) }
    withType<JavaCompile>().configureEach { it.options.release.set(target.toVersion()) }
}

internal inline fun <reified T : Task> TaskContainer.registerTask(
    vararg constructorArguments: Any,
    name: String = defaultTaskName<T>(),
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> = register(name, T::class.java, *constructorArguments).apply { configure(configure) }

internal inline fun <reified T : Task> TaskContainer.hasTask(name: String = defaultTaskName<T>()): Boolean =
    findByName(name) is T

internal inline fun <reified T : Task> TaskContainer.getTask(name: String = defaultTaskName<T>()): TaskProvider<T> {
    val existing = findByName(name)
    val jClass = T::class.java
    Errors.internal.check(existing != null) { "Task '$name' is not registered in project" }
    Errors.internal.check(existing is T) {
        "Task '$name' has type ${existing::class.java.name}, expected ${jClass.name}"
    }
    return named(name, jClass)
}
