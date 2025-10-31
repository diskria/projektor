package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.versions.Release
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange

object Ornithe : AbstractFabric(isOrnithe = true) {

    override val supportedVersionRange: MinecraftVersionRange =
        MinecraftVersionRange(MinecraftVersion.EARLIEST, Release.V_1_13_2)
}
