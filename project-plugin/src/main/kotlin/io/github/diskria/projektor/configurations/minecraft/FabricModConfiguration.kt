package io.github.diskria.projektor.configurations.minecraft

import kotlin.properties.Delegates

open class FabricModConfiguration {

    var yarnBuild: Int by Delegates.notNull()
    var apiVersion: String? = null

    internal val isApiRequired: Boolean
        get() = apiVersion != null
}
