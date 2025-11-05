package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.fabric.ornithe.Ornithe
import io.github.diskria.projektor.minecraft.loaders.fabric.quilt.Quilt
import io.github.diskria.projektor.minecraft.loaders.forge.Forge
import io.github.diskria.projektor.minecraft.loaders.forge.neoforge.NeoForge

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
        else -> failWithUnsupportedType(this::class)
    }
