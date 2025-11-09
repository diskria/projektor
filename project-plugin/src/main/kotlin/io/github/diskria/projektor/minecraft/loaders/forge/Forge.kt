package io.github.diskria.projektor.minecraft.loaders.forge

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.MinecraftConstants
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.loaders.common.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModEntryPointTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import net.minecraftforge.gradle.common.util.MinecraftExtension
import net.minecraftforge.gradle.common.util.RunConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType

object Forge : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
            val side = name.toEnumOrNull<ModSide>() ?: return@subprojects
            val sideName = side.getName()
            log("[Crafter] Configuring $sideName side...")

            ensurePluginApplied("org.jetbrains.kotlin.jvm")

            val generateModEntryPointTask = registerTask<GenerateModEntryPointTask> {
                minecraftMod = mod
                modSide = side
                outputDirectory = getBuildDirectory("generated/sources/crafter")
            }
            val (main, mixins) = configureSourceSets(
                sourceSets = sourceSets,
                mainSources = listOf(generateModEntryPointTask.map { it.outputDirectory })
            )
            forge {
                ensurePluginApplied("org.parchmentmc.librarian.forgegradle")
                mappings("official", mod.minecraftVersion.asString())
//                mappings("parchment", "${mod.config.forge.parchmentMappings}-${mod.config.forge.parchmentMinecraft}")
                dependencies {
                    minecraft("net.minecraftforge", "forge", mod.config.forge.loader)
                }
                setAccessTransformers(main.resourcesDirectory.resolve(mod.accessorConfigFileName).ensureFileExists())
                runs {
                    create(side.getName()) {
                        when (side) {
                            ModSide.CLIENT -> {
                                args(
                                    *JvmArguments.program(
                                        "username",
                                        mod.repo.owner.developer + MinecraftConstants.PLAYER_NAME_DEVELOPER_SUFFIX
                                    )
                                )
                            }

                            ModSide.SERVER -> {
                                args(*JvmArguments.program("nogui"))
                            }
                        }
                    }
                }
                tasks {
                    configureJvmTarget(mod.jvmTarget)
                    withType<JavaCompile>().configureEach {
                        options.encoding = Charsets.UTF_8.toString()
                    }
                    jar {
                        from(mixins.output)
                    }
//                    named<JavaExec>("run" + side.getName(PascalCase)) {
//                        val shadowJarTask = modProject.tasks.shadowJar.get()
//                        dependsOn(shadowJarTask)
//                        addToClasspath(shadowJarTask.archiveFile)
//
//                        javaLauncher = this@subprojects.getExtension<JavaToolchainService>().launcherFor {
//                            val javaVersion = mod.minecraftVersion.getMinJavaVersion()
//                            configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
//                        }
//                    }
                    processResources {
                        exclude(mod.accessorConfigFileName)
                    }
                }
            }
        }
        val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(getTempFile(mod.mixinsConfigFileName))
        }
        val generateModConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(getTempFile(mod.configFileName))
        }
        tasks {
            processResources {
                copyTaskOutput(generateMixinsConfigTask, mod.assetsPath)
                copyFile(rootProject.getFile(mod.iconFileName).asFile, mod.assetsPath)
                copyTaskOutput(generateModConfigTask, "META-INF")
            }
        }
        mod.config.environment.sides.forEach {
            when (it) {
                ModSide.CLIENT -> registerTask<TestClientModTask>()
                ModSide.SERVER -> registerTask<TestServerModTask>()
            }
        }
    }

    private fun MinecraftExtension.runs(configure: NamedDomainObjectContainer<RunConfig>.() -> Unit) {
        runs.configure()
    }

    private fun configureSourceSets(
        sourceSets: SourceSetContainer,
        mainSources: List<Any>
    ): Pair<SourceSet, SourceSet> {
        val main = sourceSets.main.apply { java.srcDirs(mainSources) }
        val mixins = sourceSets.create(ProjectDirectories.MINECRAFT_MIXINS).apply { addToClasspath(main) }
        return main to mixins
    }
}
