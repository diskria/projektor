package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.gradle.utils.extensions.disable
import io.github.diskria.gradle.utils.extensions.jar
import io.github.diskria.gradle.utils.extensions.projectDirectory
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.compareTo
import io.github.diskria.projektor.extensions.forge
import io.github.diskria.projektor.extensions.lazyConfigure
import io.github.diskria.projektor.extensions.minecraft
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File

object Forge : ModLoader() {

    override fun getPrepareRunTasks(project: Project, side: ModSide): List<Task> =
        listOfNotNull(project.tasks.findByName("prepareRun" + side.getName(PascalCase)))

    override fun isResourcePackConfigRequired(): Boolean = true

    override fun configure(
        mod: MinecraftMod,
        modProject: Project,
        project: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ) = with(project) {
        super.configure(mod, modProject, project, sideProjects, accessorConfigFile)
        forge {
            reobf = mod.minecraftVersion < Release.V_1_20_6
            copyIdeResources = true
            mappings("official", mod.minecraftVersion.asString())
            setAccessTransformers(accessorConfigFile)
            runs {
                sideProjects.forEach { (side, sideProject) ->
                    create(side.getName()) {
                        workingDirectory = sideProject.projectDirectory.resolve(mod.config.runDirectory).absolutePath
                        jvmArgs(
                            JvmArguments.property("mixin.debug.export", true.toString()),
                        )
                        args(
                            *JvmArguments.program("mixin.config", mod.mixinsConfigPath),
                        )
                        when (side) {
                            ModSide.CLIENT -> args(
                                *JvmArguments.program("username", mod.developerUsername),
                            )

                            ModSide.SERVER -> args(
                                *JvmArguments.program("nogui"),
                            )
                        }
                    }
                }
            }
        }
        dependencies {
            val forgeVersion = "${mod.minecraftVersion.asString()}-${mod.config.forge.loader}"
            minecraft("net.minecraftforge", "forge", forgeVersion)
        }
        tasks {
            jar {
                manifest {
                    val mixinConfigs by mod.mixinsConfigPath.autoNamedProperty(PascalCase)
                    attributes(
                        listOf(
                            mixinConfigs,
                        ).associate { it.name to it.value }
                    )
                }
            }
            lazyConfigure<Task>("makeSrcDirs") { disable() }
        }
    }
}
