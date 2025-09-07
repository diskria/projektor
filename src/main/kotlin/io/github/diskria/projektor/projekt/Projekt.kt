package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.ModLoader
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.owner.ProjektOwner
import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.extensions.appendPackageName
import io.github.diskria.utils.kotlin.extensions.common.fileName
import io.github.diskria.utils.kotlin.extensions.common.modifyIf
import io.github.diskria.utils.kotlin.extensions.setCase
import io.github.diskria.utils.kotlin.words.DotCase
import io.github.diskria.utils.kotlin.words.PascalCase
import io.github.diskria.utils.kotlin.words.PathCase
import io.github.diskria.utils.kotlin.words.SpaceCase
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

data class Projekt(
    override val owner: ProjektOwner,
    override val license: License,
    override val name: String,
    override val description: String,
    override val version: String,
    override val slug: String,
    override val packageName: String,
    override val packagePath: String = packageName.setCase(DotCase, PathCase),
    override val classNameBase: String = name.setCase(SpaceCase, PascalCase),
    override val javaVersion: Int,
    override val jvmTarget: JvmTarget,
    override val kotlinVersion: String,
    override val scm: ScmType = ScmType.GIT,
    override val softwareForge: SoftwareForgeType = owner.softwareForgeType,
) : IProjekt {

    fun toGradlePlugin(isSettingsPlugin: Boolean): GradlePlugin =
        GradlePlugin(
            id = packageName.modifyIf(isSettingsPlugin) { it.appendPackageName("settings") },
            className = classNameBase.modifyIf(isSettingsPlugin) { it + "Settings" } + "GradlePlugin",
            delegate = this,
        )

    fun toLibrary(): Library =
        Library(this)

    fun toMinecraftMod(
        modLoader: ModLoader,
        minecraftVersion: MinecraftVersion,
        environment: ModEnvironment,
        modrinthProjectUrl: String,
    ): MinecraftMod {
        val modId = slug
        return MinecraftMod(
            id = modId,
            modLoader = modLoader,
            minecraftVersion = minecraftVersion,
            environment = environment,
            modrinthProjectUrl = modrinthProjectUrl,
            mixinsConfigFileName = fileName(modId, "mixins", Constants.File.Extension.JSON),
            delegate = this,
        )
    }

    fun toAndroidApp(): AndroidApp =
        AndroidApp(this)
}
