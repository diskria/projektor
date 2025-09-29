package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.ModLoader
import io.github.diskria.projektor.minecraft.utils.ModrinthUtils
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import kotlin.properties.Delegates

open class MinecraftMod(
    private val delegate: IProjekt,
    internal val modLoader: ModLoader,
    internal val minecraftVersion: MinecraftVersion,
    internal val id: String = delegate.slug,
    internal val mixinsConfigFileName: String = fileName(id, "mixins", Constants.File.Extension.JSON)
) : IProjekt by delegate {

    var environment: ModEnvironment by Delegates.notNull()
    var isFabricApiRequired: Boolean by Delegates.notNull()
    var modrinthProjectId: String by Delegates.notNull()

    internal val modrinthProjectUrl: String
        get() = ModrinthUtils.getProjectUrl(modrinthProjectId)
}
