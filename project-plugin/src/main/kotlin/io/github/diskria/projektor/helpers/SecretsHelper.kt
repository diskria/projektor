package io.github.diskria.projektor.helpers

import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable

object SecretsHelper {
    val githubToken: String by autoNamed.environmentVariable(isRequired = true)
    val githubPackagesToken: String by autoNamed.environmentVariable(isRequired = true)

    val gpgKey: String by autoNamed.environmentVariable(isRequired = true)
    val gpgPassphrase: String by autoNamed.environmentVariable(isRequired = true)

    val sonatypeUsername: String by autoNamed.environmentVariable(isRequired = true)
    val sonatypePassword: String by autoNamed.environmentVariable(isRequired = true)

    val gradlePublishKey: String by autoNamed.environmentVariable(isRequired = true)
    val gradlePublishSecret: String by autoNamed.environmentVariable(isRequired = true)

    val modrinthToken: String by autoNamed.environmentVariable(isRequired = true)
}