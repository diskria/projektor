package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.minecraft.loaders.AbstractModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.FabricModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.LegacyFabricModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.OrnitheModLoader
import io.github.diskria.projektor.minecraft.loaders.forge.ForgeModLoader
import io.github.diskria.projektor.minecraft.loaders.forge.NeoForgeModLoader

fun ModLoaderType.mapToModel(): AbstractModLoader =
    when (this) {
        FABRIC -> FabricModLoader
        LEGACY_FABRIC -> LegacyFabricModLoader
        ORNITHE -> OrnitheModLoader
        FORGE -> ForgeModLoader
        NEOFORGE -> NeoForgeModLoader
    }

fun AbstractModLoader.mapToEnum(): ModLoaderType =
    when (this) {
        FabricModLoader -> FABRIC
        LegacyFabricModLoader -> LEGACY_FABRIC
        OrnitheModLoader -> ORNITHE
        ForgeModLoader -> FORGE
        NeoForgeModLoader -> NEOFORGE
        else -> failWithUnsupportedType(this::class)
    }
