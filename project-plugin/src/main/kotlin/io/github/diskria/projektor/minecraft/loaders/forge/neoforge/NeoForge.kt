package io.github.diskria.projektor.minecraft.loaders.forge.neoforge

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.common.minecraft.versions.common.getMinJavaVersion
import io.github.diskria.projektor.extensions.configureAdoptium
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.extensions.neoforge
import io.github.diskria.projektor.extensions.sourceSets
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.common.AbstractFabric.Companion.MIXINS_SOURCE_SET_NAME
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModEntryPointTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

data object NeoForge : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
            val side = name.toEnumOrNull<ModSide>() ?: return@subprojects
            val sideName = side.getName()
            logger.lifecycle("[Crafter] Mod side: $sideName")

            ensurePluginApplied("org.jetbrains.kotlin.jvm")

            val generateModEntryPointTask = registerTask<GenerateModEntryPointTask> {
                minecraftMod = mod
                modSide = side
                outputDirectory = getBuildDirectory("generated/sources/crafter")
            }
            val (main, mixins) = configureSourceSets(
                sideProject = this@subprojects,
                mainSrcDirs = listOf(generateModEntryPointTask.map { it.outputDirectory })
            )
            neoforge {
                version = mod.config.neoforge.loader
                parchment {
                    minecraftVersion.set(mod.config.neoforge.parchmentMinecraft)
                    mappingsVersion.set(mod.config.neoforge.parchmentMappings)
                }
                runs {
                    create(side.getName()) {
                        when (side) {
                            ModSide.CLIENT -> {
                                client()
                                programArguments.addAll(
                                    *JvmArguments.program("username", mod.repo.owner.developer + "_test"),
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
                    create("data") {
                        clientData()
                        programArguments.addAll(
                            *JvmArguments.program("mod", mod.id),
                            *JvmArguments.program("all"),
                            *JvmArguments.program("output", file("src/generated/resources").absolutePath),
                            *JvmArguments.program("existing", file("src/main/resources").absolutePath),
                        )
                    }
                }
            }
            val resourcesDirectory = projectDir.resolve("src/main/resources/META-INF")
            val accessTransformerFile = resourcesDirectory.resolve(mod.getAccessTransformerFileName())
            if (!accessTransformerFile.exists()) {
                accessTransformerFile.ensureFileExists()
            }
            tasks {
                withType<JavaCompile>().configureEach {
                    with(options) {
                        release = mod.jvmTarget.toInt()
                        encoding = Charsets.UTF_8.toString()
                    }
                }
                withType<KotlinCompile>().configureEach {
                    compilerOptions {
                        jvmTarget = mod.jvmTarget
                    }
                }
                jar {
                    from(mixins.output)
                }
                named<JavaExec>("run" + side.getName(PascalCase)) {
                    val shadowJar = modProject.tasks.shadowJar
                    dependsOn(shadowJar)
                    classpath += files(shadowJar.get().archiveFile)

                    javaLauncher = this@subprojects.getExtension<JavaToolchainService>().launcherFor {
                        configureAdoptium(mod.minSupportedVersion.getMinJavaVersion())
                    }
                }
            }
        }
        val generatedResourcesDirectory = getBuildDirectory("generated/resources").get().asFile
        val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(generatedResourcesDirectory.resolve(mod.mixinsConfigFileName))
        }
        val generateModConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(
                generatedResourcesDirectory.resolve(fileName(getLoaderName(), "mods", Constants.File.Extension.TOML))
            )
        }
        tasks {
            processResources {
                dependsOn(generateMixinsConfigTask)
                from(generateMixinsConfigTask) {
                    into("assets/${mod.id}")
                }

                from(rootProject.getFile(fileName("icon", Constants.File.Extension.PNG))) {
                    into("assets/${mod.id}")
                }

                dependsOn(generateModConfigTask)
                from(generateModConfigTask) {
                    into("META-INF")
                }
            }
        }
        mod.config.environment.sides.forEach {
            when (it) {
                ModSide.CLIENT -> modProject.registerTask<TestClientModTask>()
                ModSide.SERVER -> modProject.registerTask<TestServerModTask>()
            }
        }
    }

    private fun configureSourceSets(sideProject: Project, mainSrcDirs: List<Any>): Pair<SourceSet, SourceSet> {
        val main = sideProject.sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME) {
            java {
                srcDirs(mainSrcDirs)
            }
        }.get()
        val mixins = sideProject.sourceSets.create(MIXINS_SOURCE_SET_NAME) {
            compileClasspath += main.output + main.compileClasspath
            runtimeClasspath += main.output + main.runtimeClasspath
        }
        return main to mixins
    }
}
