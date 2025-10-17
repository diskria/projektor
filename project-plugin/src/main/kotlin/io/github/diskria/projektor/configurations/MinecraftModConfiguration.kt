package io.github.diskria.projektor.configurations

import io.github.diskria.projektor.common.configurations.IMinecraftModConfiguration
import io.github.diskria.projektor.minecraft.ModEnvironment

open class MinecraftModConfiguration : IMinecraftModConfiguration {
    var environment: ModEnvironment = ModEnvironment.CLIENT_SERVER
    var isFabricApiRequired: Boolean = false
}
