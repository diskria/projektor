package io.github.diskria.projektor.common.minecraft.era.common

import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion

enum class MojangEra(val startMinecraftVersion: MinecraftVersion) {
    NOTCH(MinecraftVersion.EARLIEST),
    MICROSOFT(Release.V_1_1),
}
