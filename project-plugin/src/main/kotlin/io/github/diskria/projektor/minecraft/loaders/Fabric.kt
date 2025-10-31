package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.versions.Release
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange

object Fabric : AbstractFabric() {

    override val supportedVersionRange: MinecraftVersionRange =
        MinecraftVersionRange(Release.V_1_14_3, MinecraftVersion.LATEST)
}
