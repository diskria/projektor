package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.minecraft.loaders.*

fun ModLoaderType.mapToModel(): ModLoader =
    when (this) {
        FABRIC -> Fabric
        ORNITHE -> Ornithe
        QUILT -> Quilt
        FORGE -> Forge
        NEOFORGE -> NeoForge
    }

fun ModLoader.mapToEnum(): ModLoaderType =
    when (this) {
        Fabric -> FABRIC
        Ornithe -> ORNITHE
        Quilt -> QUILT
        Forge -> FORGE
        NeoForge -> NEOFORGE
    }
