package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.gradle.utils.extensions.projectDirectory
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.extensions.neoforge
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import java.io.File

object Neoforge : ModLoader() {

    override fun configure(
        mod: MinecraftMod,
        modProject: Project,
        project: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ) = with(project) {
        super.configure(mod, modProject, project, sideProjects, accessorConfigFile)
        neoforge {
            version = mod.config.neoforge.loader
            parchment {
                minecraftVersion = mod.config.neoforge.minecraft
                mappingsVersion = mod.config.neoforge.mappings
            }
            setAccessTransformers(accessorConfigFile)
            runs {
                sideProjects.forEach { (side, sideProject) ->
                    create(side.getName()) {
                        gameDirectory.set(sideProject.projectDirectory.resolve(mod.config.runDirectory))
                        jvmArguments.addAll(
                            JvmArguments.property("mixin.debug.export", true.toString()),
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
