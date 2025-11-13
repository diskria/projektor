package io.github.diskria.projektor.tasks.minecraft

import io.github.diskria.gradle.utils.helpers.GradleDirectories
import org.gradle.api.tasks.bundling.Zip

abstract class ZipMultiSideMinecraftModTask : Zip() {

    init {
        group = GradleDirectories.BUILD
    }
}