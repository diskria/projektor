package io.github.diskria.projektor

import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable

object Secrets {
    val githubPackagesToken: String by autoNamed.environmentVariable()
    val gpgKey: String by autoNamed.environmentVariable()
    val gpgPassphrase: String by autoNamed.environmentVariable()
}
