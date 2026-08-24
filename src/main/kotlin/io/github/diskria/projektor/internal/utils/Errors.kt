@file:OptIn(ExperimentalContracts::class)

package io.github.diskria.projektor.internal.utils

import org.gradle.api.GradleException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal object Errors {

    val frontend = object : ErrorScope {
        override fun error(message: String): Nothing = throw GradleException(message)
    }

    val internal = object : ErrorScope {
        override fun error(message: String): Nothing {
            val fullMessage = "$message. (This is a Projektor internal error, please report it!)"
            throw IllegalStateException(fullMessage)
        }
    }
}

internal interface ErrorScope {
    fun error(message: String): Nothing
}

internal inline fun ErrorScope.check(value: Boolean, lazyMessage: () -> Any) {
    contract { returns() implies value }
    if (!value) {
        error(lazyMessage().toString())
    }
}

internal inline fun ErrorScope.require(value: Boolean, lazyMessage: () -> Any) {
    contract { returns() implies value }
    if (!value) {
        error(lazyMessage().toString())
    }
}

internal inline fun <T : Any> ErrorScope.checkNotNull(value: T?, lazyMessage: () -> Any): T {
    contract { returns() implies (value != null) }
    if (value == null) {
        error(lazyMessage().toString())
    }
    return value
}

internal inline fun <T : Any> ErrorScope.requireNotNull(value: T?, lazyMessage: () -> Any): T {
    contract { returns() implies (value != null) }
    if (value == null) {
        error(lazyMessage().toString())
    }
    return value
}
