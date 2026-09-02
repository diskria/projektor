package io.github.diskria.projektor.extensions

import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.services.BuildServiceRegistry
import org.gradle.api.services.BuildServiceSpec
import org.gradle.kotlin.dsl.registerIfAbsent

@PublishedApi
internal inline fun <reified S : BuildService<*>> defaultBuildServiceName(): String =
    defaultNameBySuffix<S>("BuildService")

inline fun <reified S : BuildService<P>, P : BuildServiceParameters> BuildServiceRegistry.register(
    name: String = defaultBuildServiceName<S>(),
    crossinline configure: BuildServiceSpec<P>.() -> Unit = {}
) {
    registerIfAbsent(name, S::class) { it.configure() }
}

internal inline fun <reified S : BuildService<*>> BuildServiceRegistry.has(
    name: String = defaultBuildServiceName<S>()
): Boolean = registrations.findByName(name)?.service?.orNull is S

@Suppress("UNCHECKED_CAST")
internal inline fun <reified S : BuildService<*>> BuildServiceRegistry.get(
    name: String = defaultBuildServiceName<S>()
): S = (registrations.findByName(name)?.service as Provider<S>).get()

internal inline fun <reified S : BuildService<*>> BuildServiceRegistry.find(
    name: String = defaultBuildServiceName<S>()
): S? = if (has<S>(name)) get<S>(name) else null
