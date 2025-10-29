package io.github.diskria.projektor.common

import io.github.diskria.gradle.utils.extensions.common.buildGradleProjectPath

object ProjectModules {

    object Common {
        const val NAME: String = "common"
        val PATH: String = buildGradleProjectPath(NAME)
    }
}