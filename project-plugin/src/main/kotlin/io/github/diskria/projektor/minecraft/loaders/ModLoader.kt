package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.getResourcePackFormat
import io.github.diskria.projektor.common.minecraft.versions.minJavaVersion
import io.github.diskria.projektor.extensions.copyFile
import io.github.diskria.projektor.extensions.copyTaskOutput
import io.github.diskria.projektor.extensions.lazyConfigure
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.mixins
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateResourcePackConfigTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import java.io.File

abstract class ModLoader {

    val family: ModLoaderFamily
        get() = ModLoaderFamily.of(mapToEnum())

    open fun configure(
        mod: MinecraftMod,
        modProject: Project,
        project: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ): Any = with(project) {
        modProject.findCommonProject()?.let { commonProject ->
            dependencies {
                compileOnly(commonProject)
            }
            tasks {
                jar {
                    from(commonProject.sourceSets.main.output)
                }
            }
        }
        tasks {
            withType<AbstractCopyTask> {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            sideProjects.keys.forEach { side ->
                lazyConfigure<JavaExec>("run" + side.getName(PascalCase)) {
                    addToClasspath(jar.get().archiveFile)
                    javaLauncher = this@with.getExtension<JavaToolchainService>().launcherFor {
                        val javaVersion = mod.minecraftVersion.minJavaVersion
                        configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
                    }
                }
            }
            if (isResourcePackConfigRequired()) {
                val generateResourcePackConfigTask = registerTask<GenerateResourcePackConfigTask> {
                    minecraftMod = mod
                    outputFile = getTempFile(mod.resourcePackConfigFileName)
                    minFormat = mod.minSupportedVersion.getResourcePackFormat(project)
                    maxFormat = mod.maxSupportedVersion.getResourcePackFormat(project)
                }
                processResources {
                    copyTaskOutput(generateResourcePackConfigTask)
                }
            }
            val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
                minecraftMod = mod
                sideMixinSourceSetDirectories = sideProjects.mapValues { it.value.sourceSets.mixins.java.srcDirs.first() }
                outputFile = getTempFile(mod.mixinsConfigFileName)
            }
            val generateModConfigTask = registerTask<GenerateModConfigTask> {
                minecraftMod = mod
                outputFile = getTempFile(mod.configFileName)
            }
            processResources {
                copyTaskOutput(generateMixinsConfigTask, mod.assetsPath)
                copyTaskOutput(generateModConfigTask, mod.configFileParentPath)
                copyFile(modProject.getFile(mod.iconFileName).asFile, mod.assetsPath)
            }
        }
    }

    open fun getPrepareRunTasks(project: Project, side: ModSide): List<Task> = emptyList()

    open fun getAccessorConfigTemplate(): String = Constants.Char.EMPTY

    open fun isResourcePackConfigRequired(): Boolean = false

    fun getLoaderName(): String = mapToEnum().getName()

    fun getLoaderDisplayName(): String = this::class.className()
}
