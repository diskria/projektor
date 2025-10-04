package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.camelCase
import io.github.diskria.kotlin.utils.extensions.setCase
import kotlin.properties.ReadOnlyProperty

object Secrets {

    val githubPackagesToken: String? by environmentVariableDelegate()
    val gpgKey: String? by environmentVariableDelegate()
    val gpgPassphrase: String? by environmentVariableDelegate()

    private fun environmentVariableDelegate(): ReadOnlyProperty<Any?, String?> =
        ReadOnlyProperty { _, property ->
            System.getenv(
                property
                    .name
                    .takeIf { it.isNotBlank() }
                    ?.setCase(camelCase, SCREAMING_SNAKE_CASE)
            )
        }
}
