package io.github.diskria.projektor.extensions

import org.gradle.api.PolymorphicDomainObjectContainer

inline fun <reified T : Any> PolymorphicDomainObjectContainer<in T>.create(
    name: String,
    noinline configure: (T) -> Unit
): T = create(name, T::class.java, configure)

inline fun <reified T : Any> PolymorphicDomainObjectContainer<in T>.getOrCreate(
    name: String,
    noinline configure: (T) -> Unit
): T = (findByName(name) as? T) ?: create<T>(name, configure)
