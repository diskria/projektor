package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.modifyIf

open class GradlePlugin(private val projekt: IProjekt) : IProjekt by projekt {

    var isSettingsPlugin: Boolean = false
    var tags: Set<String> = emptySet()

    override val packageName: String
        get() = projekt.packageName.modifyIf(isSettingsPlugin) { it.appendPackageName("settings") }
}
