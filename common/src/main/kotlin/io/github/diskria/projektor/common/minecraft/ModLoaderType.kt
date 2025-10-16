package io.github.diskria.projektor.common.minecraft

import io.github.diskria.kotlin.utils.extensions.common.flatcase
import io.github.diskria.kotlin.utils.extensions.mappers.getName

enum class ModLoaderType {
    FABRIC,
    QUILT,
    FORGE,
    NEOFORGE,
}

fun ModLoaderType.getDirectoryName(): String =
    getName(flatcase)
