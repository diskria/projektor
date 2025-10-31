package io.github.diskria.projektor.minecraft

import io.github.diskria.projektor.common.minecraft.ModSide

enum class ModEnvironment(val sides: List<ModSide>) {
    CLIENT_SERVER(
        listOf(ModSide.CLIENT, ModSide.SERVER)
    ),
    DEDICATED_SERVER_ONLY(
        listOf(ModSide.SERVER)
    ),
    CLIENT_ONLY(
        listOf(ModSide.CLIENT)
    ),
}
