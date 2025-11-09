package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.fabric.LegacyFabric
import io.github.diskria.projektor.minecraft.loaders.fabric.Ornithe
import io.github.diskria.projektor.minecraft.loaders.forge.Forge
import io.github.diskria.projektor.minecraft.loaders.forge.Neoforge

fun ModLoaderType.mapToModel(): ModLoader =
    when (this) {
        FABRIC -> Fabric
        LEGACY_FABRIC -> LegacyFabric
        ORNITHE -> Ornithe
        FORGE -> Forge
        NEOFORGE -> Neoforge
    }

fun ModLoader.mapToEnum(): ModLoaderType =
    when (this) {
        Fabric -> FABRIC
        LegacyFabric -> LEGACY_FABRIC
        Ornithe -> ORNITHE
        Forge -> FORGE
        Neoforge -> NEOFORGE
        else -> failWithUnsupportedType(this::class)
    }
