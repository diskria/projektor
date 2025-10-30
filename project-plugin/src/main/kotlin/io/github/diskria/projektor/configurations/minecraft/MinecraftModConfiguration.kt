package io.github.diskria.projektor.configurations.minecraft

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.minecraft.ModEnvironment

open class MinecraftModConfiguration {

    var environment: ModEnvironment = ModEnvironment.CLIENT_SERVER
    var maxSupportedVersion: MinecraftVersion? = null

    val fabric: FabricModConfiguration
        get() = fabricModConfiguration ?: gradleError("Fabric mod not configured")

    private var fabricModConfiguration: FabricModConfiguration? = null

    fun fabric(configuration: FabricModConfiguration.() -> Unit = {}) {
        if (fabricModConfiguration != null) {
            gradleError("Fabric mod already configured")
        }
        fabricModConfiguration = FabricModConfiguration().apply(configuration)
    }

    val ornithe: OrnitheModConfiguration
        get() = ornitheModConfiguration ?: gradleError("Fabric mod not configured")

    private var ornitheModConfiguration: OrnitheModConfiguration? = null

    fun ornithe(configuration: OrnitheModConfiguration.() -> Unit = {}) {
        if (ornitheModConfiguration != null) {
            gradleError("Fabric mod already configured")
        }
        ornitheModConfiguration = OrnitheModConfiguration().apply(configuration)
    }
}
