package io.github.diskria.projektor.minecraft.config

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.minecraft.ModSourceSet
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@EncodeDefaults
@PrettyPrint
class MixinsConfig(
    @SerialName("required")
    val isRequired: Boolean = true,

    @SerialName("package")
    val packageName: String,

    @SerialName("compatibilityLevel")
    val javaTargetVersion: String,

    @SerialName("injectors")
    val injectorConfig: InjectorConfig = InjectorConfig(),

    @SerialName("overwrites")
    val overwriteConfig: OverwriteConfig = OverwriteConfig(),

    @SerialName("mixins")
    val mainMixins: List<String>? = null,

    @SerialName("client")
    val clientMixins: List<String>? = null,
) {
    @Serializable
    data class InjectorConfig(val defaultRequire: Int = 1)

    @Serializable
    data class OverwriteConfig(val requireAnnotations: Boolean = true)

    companion object {
        fun of(mod: MinecraftMod, mixinsBySourceSet: Map<ModSourceSet, List<String>>): MixinsConfig =
            MixinsConfig(
                packageName = mod.packageName.appendPackageName("mixins"),
                javaTargetVersion = "JAVA_${mod.jvmTarget.toInt()}",
                mainMixins = mixinsBySourceSet[ModSourceSet.MAIN],
                clientMixins = mixinsBySourceSet[ModSourceSet.CLIENT],
            )
    }
}
