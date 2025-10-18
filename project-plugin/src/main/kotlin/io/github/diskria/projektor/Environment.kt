package io.github.diskria.projektor

import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable

object Environment {

    private val ci: String by autoNamed.environmentVariable()

    fun isCI(): Boolean = ci.toBoolean()

    object Secrets {
        val githubToken: String by autoNamed.environmentVariable(isRequired = true)
        val githubPackagesToken: String by autoNamed.environmentVariable(isRequired = true)
        val gpgKey: String by autoNamed.environmentVariable(isRequired = true)
        val gpgPassphrase: String by autoNamed.environmentVariable(isRequired = true)
        val sonatypeUsername: String by autoNamed.environmentVariable(isRequired = true)
        val sonatypePassword: String by autoNamed.environmentVariable(isRequired = true)
    }
}
