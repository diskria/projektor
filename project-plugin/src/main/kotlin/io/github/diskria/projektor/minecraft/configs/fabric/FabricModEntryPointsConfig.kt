package io.github.diskria.projektor.minecraft.configs.fabric

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class FabricModEntryPointsConfig private constructor(
    @SerialName("main")
    val mainEntryPoints: List<String>? = null,

    @SerialName("client")
    val clientEntryPoints: List<String>? = null,

    @SerialName("server")
    val serverEntryPoints: List<String>? = null,
) {
    companion object {
        fun of(mod: MinecraftMod): FabricModEntryPointsConfig {
            val clientPackageName = mod.packageName.appendPackageName(ModSide.CLIENT.getName())
            val serverPackageName = mod.packageName.appendPackageName(ModSide.SERVER.getName())
            val clientEntryPoint = clientPackageName.appendPackageName(mod.getEntryPointName(ModSide.CLIENT))
            val serverEntryPoint = serverPackageName.appendPackageName(mod.getEntryPointName(ModSide.SERVER))
            return when (mod.config.environment) {
                ModEnvironment.CLIENT_SERVER -> FabricModEntryPointsConfig(
                    mainEntryPoints = listOf(serverEntryPoint),
                    clientEntryPoints = listOf(clientEntryPoint),
                )

                ModEnvironment.CLIENT -> FabricModEntryPointsConfig(
                    clientEntryPoints = listOf(clientEntryPoint),
                )

                ModEnvironment.DEDICATED_SERVER -> FabricModEntryPointsConfig(
                    serverEntryPoints = listOf(serverEntryPoint),
                )
            }
        }
    }
}
