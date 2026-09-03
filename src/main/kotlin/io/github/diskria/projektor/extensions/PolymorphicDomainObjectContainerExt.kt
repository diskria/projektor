package io.github.diskria.projektor.extensions

import org.gradle.api.PolymorphicDomainObjectContainer

internal inline fun <reified T : Any> PolymorphicDomainObjectContainer<in T>.create(
    name: String,
    noinline configure: (T) -> Unit
): T = create(name, T::class.java, configure)
