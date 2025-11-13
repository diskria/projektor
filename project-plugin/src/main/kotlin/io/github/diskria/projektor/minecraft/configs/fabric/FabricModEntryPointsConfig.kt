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
        fun of(mod: MinecraftMod, side: ModSide?): FabricModEntryPointsConfig {
            val clientEntryPoints = FabricModEntryPointsConfig(
                clientEntryPoints = listOf(buildPackageName(mod, ModSide.CLIENT))
            )
            val serverEntryPoints = FabricModEntryPointsConfig(
                serverEntryPoints = listOf(buildPackageName(mod, ModSide.SERVER))
            )
            return when (side) {
                ModSide.CLIENT -> clientEntryPoints
                ModSide.SERVER -> serverEntryPoints
                null -> when (mod.config.environment) {
                    ModEnvironment.CLIENT_SERVER -> FabricModEntryPointsConfig(
                        mainEntryPoints = listOf(buildPackageName(mod, ModSide.SERVER)),
                        clientEntryPoints = listOf(buildPackageName(mod, ModSide.CLIENT)),
                    )

                    ModEnvironment.CLIENT -> clientEntryPoints
                    ModEnvironment.DEDICATED_SERVER -> serverEntryPoints
                }
            }
        }

        private fun buildPackageName(mod: MinecraftMod, side: ModSide): String =
            mod.packageName
                .appendPackageName(side.getName())
                .appendPackageName(mod.getEntryPointName(side))
    }
}
