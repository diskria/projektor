package io.github.diskria.projektor.minecraft.configs.ornithe

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class OrnitheModEntryPointsConfig private constructor(
    @SerialName("init")
    val initEntryPoints: List<String>? = null,
) {
    companion object {
        fun of(mod: MinecraftMod): OrnitheModEntryPointsConfig {
            val classPathPrefix = mod.packageName.appendPackageName(mod.classNamePrefix)
            return OrnitheModEntryPointsConfig(listOf(classPathPrefix + "Mod"))
        }
    }
}
