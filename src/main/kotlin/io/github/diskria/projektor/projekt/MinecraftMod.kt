package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.ModLoader
import io.github.diskria.projektor.minecraft.version.MinecraftVersion

class MinecraftMod(
    val id: String,
    val modLoader: ModLoader,
    val minecraftVersion: MinecraftVersion,
    val environment: ModEnvironment,
    val modrinthProjectUrl: String,
    val mixinsConfigFileName: String,
    private val delegate: IProjekt,
) : IProjekt by delegate