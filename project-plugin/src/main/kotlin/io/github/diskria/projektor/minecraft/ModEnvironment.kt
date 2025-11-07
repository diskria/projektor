package io.github.diskria.projektor.minecraft

import io.github.diskria.projektor.common.minecraft.sides.ModSide

enum class ModEnvironment(val sides: List<ModSide>) {

    CLIENT_SERVER(
        listOf(ModSide.CLIENT, ModSide.SERVER)
    ),
    CLIENT(
        listOf(ModSide.CLIENT)
    ),
    DEDICATED_SERVER(
        listOf(ModSide.SERVER)
    );

    fun getSingleSideOrNull(): ModSide? =
        sides.singleOrNull()
}
