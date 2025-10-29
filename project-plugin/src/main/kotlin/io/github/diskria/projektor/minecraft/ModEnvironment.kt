package io.github.diskria.projektor.minecraft

import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.supportsEnvironmentSplit

enum class ModEnvironment(
    val fabricConfigValue: String,
    val forgeConfigValue: String,
    val sides: List<ModSide>,
) {
    CLIENT_SERVER(
        "*",
        "BOTH",
        listOf(ModSide.CLIENT, ModSide.SERVER)
    ),
    CLIENT_SIDE_ONLY(
        "client",
        "CLIENT",
        listOf(ModSide.CLIENT)
    ),
    SERVER_SIDE_ONLY(
        "server",
        "SERVER",
        listOf(ModSide.SERVER)
    ),
}

fun ModEnvironment.getSourceSets(minecraftVersion: MinecraftVersion): List<ModSourceSet> =
    buildList {
        add(ModSourceSet.MAIN)
        if (minecraftVersion.supportsEnvironmentSplit() && sides.contains(ModSide.CLIENT)) {
            add(ModSourceSet.CLIENT)
        }
    }
