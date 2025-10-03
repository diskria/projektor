package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import org.gradle.api.Project

open class GradlePlugin(private val projekt: IProjekt, project: Project) : IProjekt by projekt {

    var isSettingsPlugin: Boolean = false
    var tags: Set<String> = emptySet()

    override val packageName: String =
        projekt.packageName.modifyIf(isSettingsPlugin) { it.appendPackageName("settings") }
}
