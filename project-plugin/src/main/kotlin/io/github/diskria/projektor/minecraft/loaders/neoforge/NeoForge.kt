package io.github.diskria.projektor.minecraft.loaders.neoforge

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.MinecraftConstants
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.getMinJavaVersion
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.loaders.common.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModEntryPointTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

data object NeoForge : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
            val side = name.toEnumOrNull<ModSide>() ?: return@subprojects
            val sideName = side.getName()
            log("[Crafter] Configuring $sideName side...")

            ensureKotlinPluginsApplied()

            val generateModEntryPointTask = registerTask<GenerateModEntryPointTask> {
                minecraftMod = mod
                modSide = side
                outputDirectory = getBuildDirectory("generated/sources/crafter")
            }
            val (main, mixins) = configureSourceSets(
                sourceSets = sourceSets,
                mainSources = listOf(generateModEntryPointTask.map { it.outputDirectory })
            )
            neoforge {
                version = mod.config.neoforge.loader
                parchment {
                    minecraftVersion.set(mod.config.neoforge.parchmentMinecraft)
                    mappingsVersion.set(mod.config.neoforge.parchmentMappings)
                }
                setAccessTransformers(main.resourcesDirectory.resolve(mod.accessorConfigFileName).ensureFileExists())
                runs {
                    create(side.getName()) {
                        when (side) {
                            ModSide.CLIENT -> {
                                client()
                                programArguments.addAll(
                                    *JvmArguments.program(
                                        "username",
                                        mod.repo.owner.developer + MinecraftConstants.PLAYER_NAME_DEVELOPER_SUFFIX
                                    ),
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
                            *JvmArguments.program("existing", main.resourcesDirectory.absolutePath),
                        )
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
                named<JavaExec>("run" + side.getName(PascalCase)) {
                    val shadowJarTask = modProject.tasks.shadowJar.get()
                    dependsOn(shadowJarTask)
                    addToClasspath(shadowJarTask.archiveFile)

                    javaLauncher = this@subprojects.getExtension<JavaToolchainService>().launcherFor {
                        val javaVersion = mod.minecraftVersion.getMinJavaVersion()
                        configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
                    }
                }
                processResources {
                    exclude(mod.accessorConfigFileName)
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

    private fun configureSourceSets(
        sourceSets: SourceSetContainer,
        mainSources: List<Any>
    ): Pair<SourceSet, SourceSet> {
        val main = sourceSets.main.apply { java.srcDirs(mainSources) }
        val mixins = sourceSets.create(ProjectDirectories.MINECRAFT_MIXINS).apply { addToClasspath(main) }
        return main to mixins
    }
}
