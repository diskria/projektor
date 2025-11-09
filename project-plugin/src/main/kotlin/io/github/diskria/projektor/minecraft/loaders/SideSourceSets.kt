package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.projektor.common.minecraft.sides.ModSide
import org.gradle.api.tasks.SourceSet

class SideSourceSets(val side: ModSide, val main: SourceSet, val mixins: SourceSet) {

    companion object {
        const val MIXINS_NAME: String = "mixins"
    }
}
