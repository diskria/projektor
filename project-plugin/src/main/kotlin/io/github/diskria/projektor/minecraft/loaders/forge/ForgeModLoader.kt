package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.gradle.utils.extensions.disable
import io.github.diskria.gradle.utils.extensions.getTaskOrNull
import io.github.diskria.gradle.utils.extensions.jar
import io.github.diskria.gradle.utils.extensions.projectDirectory
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
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
import io.github.diskria.projektor.minecraft.loaders.AbstractModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File

object ForgeModLoader : AbstractModLoader() {

    override fun getPrepareRunTasks(pluginProject: Project, side: ModSide): List<Task> =
        listOfNotNull(pluginProject.getTaskOrNull("prepareRun" + side.getName(PascalCase)))

    override fun isResourcePackConfigRequired(): Boolean = true

    override fun configure(
        mod: MinecraftMod,
        modProject: Project,
        pluginProject: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ) = with(pluginProject) {
        super.configure(mod, modProject, pluginProject, sideProjects, accessorConfigFile)
        forge {
            reobf = mod.minecraftVersion < Release.V_1_20_6
            mappings("official", mod.minecraftVersion.asString())
            setAccessTransformer(accessorConfigFile)
            runs {
                sideProjects.forEach { (side, project) ->
                    val runDirectory = project.projectDirectory.resolve(mod.config.runDirectoryName)
                    create(side.getName()) {
                        workingDirectory(runDirectory)
                        val memoryRange = when (side) {
                            ModSide.CLIENT -> 2..4
                            ModSide.SERVER -> 4..8
                        }
                        jvmArgs(
                            *JvmArguments.memory(memoryRange, Size.GIGABYTES),
                            JvmArguments.property("mixin.debug.export", true),
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
