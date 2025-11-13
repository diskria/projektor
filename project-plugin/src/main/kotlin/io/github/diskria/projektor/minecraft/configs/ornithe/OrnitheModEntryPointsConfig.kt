package io.github.diskria.projektor.minecraft.configs.ornithe

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class OrnitheModEntryPointsConfig private constructor(
    @SerialName("init")
    val initEntryPoints: List<String>? = null,

    @SerialName("client")
    val clientEntryPoints: List<String>? = null,

    @SerialName("server")
    val serverEntryPoints: List<String>? = null,
) {
    companion object {
        fun of(mod: MinecraftMod, singleSide: ModSide?): OrnitheModEntryPointsConfig =
            if (singleSide != null) {
                OrnitheModEntryPointsConfig(
                    initEntryPoints = listOf(buildPackageName(mod, singleSide)),
                )
            } else {
                when (mod.config.environment) {
                    ModEnvironment.CLIENT_SERVER -> OrnitheModEntryPointsConfig(
                        initEntryPoints = listOf(buildPackageName(mod, ModSide.SERVER)),
                        clientEntryPoints = listOf(buildPackageName(mod, ModSide.CLIENT)),
                    )

                    ModEnvironment.CLIENT -> OrnitheModEntryPointsConfig(
                        clientEntryPoints = listOf(buildPackageName(mod, ModSide.CLIENT))
                    )

                    ModEnvironment.DEDICATED_SERVER -> OrnitheModEntryPointsConfig(
                        serverEntryPoints = listOf(buildPackageName(mod, ModSide.SERVER))
                    )
                }
            }

        private fun buildPackageName(mod: MinecraftMod, side: ModSide): String =
            mod.packageName
                .appendPackageName(side.getName())
                .appendPackageName(mod.getEntryPointName(side))
    }
}
