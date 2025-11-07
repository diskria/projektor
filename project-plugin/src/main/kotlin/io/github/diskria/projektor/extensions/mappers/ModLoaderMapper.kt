package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.minecraft.loaders.babric.Babric
import io.github.diskria.projektor.minecraft.loaders.common.ModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.forge.Forge
import io.github.diskria.projektor.minecraft.loaders.legacyfabric.LegacyFabric
import io.github.diskria.projektor.minecraft.loaders.neoforge.NeoForge
import io.github.diskria.projektor.minecraft.loaders.ornithe.Ornithe

fun ModLoaderType.mapToModel(): ModLoader =
    when (this) {
        FABRIC -> Fabric
        LEGACY_FABRIC -> LegacyFabric
        ORNITHE -> Ornithe
        BABRIC -> Babric
        FORGE -> Forge
        NEOFORGE -> NeoForge
    }

fun ModLoader.mapToEnum(): ModLoaderType =
    when (this) {
        Fabric -> FABRIC
        LegacyFabric -> LEGACY_FABRIC
        Ornithe -> ORNITHE
        Babric -> BABRIC
        Forge -> FORGE
        NeoForge -> NEOFORGE
        else -> failWithUnsupportedType(this::class)
    }
