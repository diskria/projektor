package io.github.diskria.projektor

import io.github.diskria.kotlin.utils.properties.AutoNamedEnvironmentVariable

object Secrets {
    val githubPackagesToken: String by AutoNamedEnvironmentVariable()
    val gpgKey: String by AutoNamedEnvironmentVariable()
    val gpgPassphrase: String by AutoNamedEnvironmentVariable()
}
