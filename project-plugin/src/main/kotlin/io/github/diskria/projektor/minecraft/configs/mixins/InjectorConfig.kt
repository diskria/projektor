package io.github.diskria.projektor.minecraft.configs.mixins

import kotlinx.serialization.Serializable

@Serializable
data class InjectorConfig(
    val defaultRequire: Int,
) {
    companion object {
        fun newInstance(): InjectorConfig =
            InjectorConfig(
                defaultRequire = 1,
            )
    }
}
