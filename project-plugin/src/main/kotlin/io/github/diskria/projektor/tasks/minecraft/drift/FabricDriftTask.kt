package io.github.diskria.projektor.tasks.minecraft.drift

import io.github.diskria.projektor.minecraft.loaders.Fabric
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.tasks.minecraft.drift.common.AbstractDriftTask

abstract class FabricDriftTask : AbstractDriftTask() {

    override fun getLoader(): ModLoader = Fabric
}