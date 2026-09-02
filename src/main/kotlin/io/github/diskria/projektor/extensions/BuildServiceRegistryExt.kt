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

@Suppress("UNCHECKED_CAST")
internal inline fun <reified S : BuildService<*>> BuildServiceRegistry.findByType(
    name: String = defaultBuildServiceName<S>()
): S? = (registrations.findByName(name)?.service as? Provider<S>)?.orNull
