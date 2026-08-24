package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.internal.utils.Errors
import org.gradle.api.provider.ProviderFactory

internal val ProviderFactory.isCI: Boolean
    get() = env("CI")?.toBoolean() ?: false

internal fun ProviderFactory.env(name: String): String? =
    environmentVariable(name).orNull

internal fun ProviderFactory.requireEnv(name: String): String =
    env(name) ?: Errors.internal.error("Environment variable '$name' is required but not set!")
