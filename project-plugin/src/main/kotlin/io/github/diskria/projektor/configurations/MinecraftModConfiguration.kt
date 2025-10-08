package io.github.diskria.projektor.configurations

import io.github.diskria.projektor.minecraft.ModEnvironment
import kotlin.properties.Delegates

open class MinecraftModConfiguration {
    var modrinthProjectId: String by Delegates.notNull()
    var environment: ModEnvironment = ModEnvironment.CLIENT_SERVER
    var isFabricApiRequired: Boolean = false
}
