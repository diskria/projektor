package io.github.diskria.projektor.common.minecraft.era.common

import io.github.diskria.gradle.utils.helpers.jvm.JavaConstants
import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.compareTo

object JavaCompatibility {

    private val REQUIREMENTS: Map<MinecraftVersion, Int> = mapOf(
        MinecraftVersion.EARLIEST to JavaConstants.VERSION_8,
        Release.V_1_17 to 16,
        Release.V_1_18 to 17,
        Release.V_1_20_5 to 21,
    )

    fun getMinJavaVersion(minecraftVersion: MinecraftVersion): Int =
        REQUIREMENTS.entries
            .sortedWith(compareByDescending(MinecraftVersion.COMPARATOR) { it.key })
            .first { minecraftVersion >= it.key }
            .value
}
