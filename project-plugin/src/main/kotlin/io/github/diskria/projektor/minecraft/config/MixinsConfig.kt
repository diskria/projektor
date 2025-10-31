package io.github.diskria.projektor.minecraft.config

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
@EncodeDefaults
@PrettyPrint
class MixinsConfig(
    @SerialName("required")
    val isRequired: Boolean = true,

    @SerialName("package")
    val packageName: String,

    @SerialName("compatibilityLevel")
    val jvmTargetVersion: String,

    @SerialName("injectors")
    val injectorConfig: InjectorConfig = InjectorConfig(),

    @SerialName("overwrites")
    val overwriteConfig: OverwriteConfig = OverwriteConfig(),

    @SerialName("mixins")
    val mainMixins: List<String>? = null,

    @SerialName("client")
    val clientMixins: List<String>? = null,

    @SerialName("server")
    val serverMixins: List<String>? = null,
) {
    @Serializable
    data class InjectorConfig(val defaultRequire: Int = 1)

    @Serializable
    data class OverwriteConfig(val requireAnnotations: Boolean = true)

    companion object {
        fun of(mod: MinecraftMod, sideMixins: Map<ModSide, List<String>>): MixinsConfig =
            MixinsConfig(
                packageName = mod.packageName.appendPackageName("mixins"),
                jvmTargetVersion = "JAVA_${mod.jvmTarget.toInt()}",
                mainMixins = when (mod.config.environment) {
                    ModEnvironment.DEDICATED_SERVER_ONLY -> null
                    else -> sideMixins[ModSide.SERVER]
                },
                clientMixins = sideMixins[ModSide.CLIENT],
                serverMixins = when (mod.config.environment) {
                    ModEnvironment.DEDICATED_SERVER_ONLY -> sideMixins[ModSide.SERVER]
                    else -> null
                },
            )
    }
}
