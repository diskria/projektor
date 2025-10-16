package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.projektor.common.minecraft.ModLoaderType
import io.github.diskria.projektor.common.minecraft.ModLoaderType.*
import io.github.diskria.projektor.minecraft.loaders.*

fun ModLoaderType.mapToModel(): ModLoader =
    when (this) {
        FABRIC -> Fabric
        QUILT -> Quilt
        FORGE -> Forge
        NEOFORGE -> NeoForge
    }

fun ModLoader.mapToEnum(): ModLoaderType =
    when (this) {
        Fabric -> FABRIC
        Quilt -> QUILT
        Forge -> FORGE
        NeoForge -> NEOFORGE
    }
