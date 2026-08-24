package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.core.configurators.toVersion
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.check
import io.github.diskria.projektor.internal.utils.decapitalized
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
internal val Class<*>.taskName: String get() = simpleName.removeSuffix("Task").decapitalized()

internal fun TaskContainer.jar(configure: Jar.() -> Unit): TaskProvider<Jar> =
    named<Jar>("jar", configure)

internal fun TaskContainer.configureJvmTarget(target: JvmTarget) {
    withType<KotlinCompile>().configureEach { it.compilerOptions.jvmTarget.set(target) }
    withType<JavaCompile>().configureEach { it.options.release.set(target.toVersion()) }
}

internal inline fun <reified T : Task> TaskContainer.registerTask(
    vararg constructorArguments: Any,
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> {
    val jClass = T::class.java
    val name = jClass.taskName
    return register(name, jClass, *constructorArguments).apply { configure(configure) }
}

internal inline fun <reified T : Task> TaskContainer.ensureTaskRegistered(
    vararg constructorArguments: Any,
    noinline configure: T.() -> Unit = {}
): TaskProvider<T> {
    val jClass = T::class.java
    val name = jClass.taskName
    return runCatching { named(name, jClass) }.getOrElse {
        registerTask(constructorArguments = constructorArguments, configure)
    }
}

internal inline fun <reified T : Task> TaskContainer.getTask(): TaskProvider<T> {
    val jClass = T::class.java
    val name = jClass.taskName
    val existing = findByName(name)
    Errors.internal.check(existing != null) { "Task '$name' is not registered in project" }
    Errors.internal.check(existing is T) { "Task '$name' has type ${jClass.name}, expected ${jClass.name}" }
    return named(name, jClass)
}
