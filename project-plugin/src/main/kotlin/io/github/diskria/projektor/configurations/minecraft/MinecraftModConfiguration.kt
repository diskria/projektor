package io.github.diskria.projektor.configurations.minecraft

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.minecraft.ModEnvironment

open class MinecraftModConfiguration {

    val fabric: FabricModConfiguration
        get() = fabricModConfiguration ?: gradleError("Fabric mod not configured")

    var environment: ModEnvironment = ModEnvironment.CLIENT_SERVER
    var maxSupportedVersion: MinecraftVersion? = null

    private var fabricModConfiguration: FabricModConfiguration? = null

    fun fabric(configuration: FabricModConfiguration.() -> Unit = {}) {
        if (fabricModConfiguration != null) {
            gradleError("Fabric mod already configured")
        }
        fabricModConfiguration = FabricModConfiguration().apply(configuration)
    }
}
