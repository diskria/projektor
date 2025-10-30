package io.github.diskria.projektor.tasks.minecraft.drift.common

import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.Ornithe

abstract class OrnitheDriftTask : AbstractDriftTask() {

    override fun getLoader(): ModLoader = Ornithe
}
