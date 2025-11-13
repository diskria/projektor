package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.gradle.utils.extensions.projectDirectory
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.extensions.neoforge
import io.github.diskria.projektor.minecraft.loaders.AbstractModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import java.io.File

object NeoForgeModLoader : AbstractModLoader() {

    override fun configure(
        mod: MinecraftMod,
        modProject: Project,
        pluginProject: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ) = with(pluginProject) {
        super.configure(mod, modProject, pluginProject, sideProjects, accessorConfigFile)
        neoforge {
            version = mod.config.neoforge.loader
            parchment {
                minecraftVersion = mod.config.neoforge.minecraft
                mappingsVersion = mod.config.neoforge.mappings
            }
            setAccessTransformers(accessorConfigFile)
            runs {
                sideProjects.forEach { (side, project) ->
                    val runDirectory = project.projectDirectory.resolve(mod.config.runDirectoryName)
                    create(side.getName()) {
                        gameDirectory = runDirectory
                        val memoryRange = when (side) {
                            ModSide.CLIENT -> 2..4
                            ModSide.SERVER -> 4..8
                        }
                        jvmArguments.addAll(
                            *JvmArguments.memory(memoryRange, Size.GIGABYTES),
                            JvmArguments.property("mixin.debug.export", true),
                        )
                        when (side) {
                            ModSide.CLIENT -> {
                                client()
                                programArguments.addAll(
                                    *JvmArguments.program("username", mod.developerUsername),
                                )
                            }

                            ModSide.SERVER -> {
                                server()
                                programArguments.addAll(
                                    *JvmArguments.program("nogui"),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
