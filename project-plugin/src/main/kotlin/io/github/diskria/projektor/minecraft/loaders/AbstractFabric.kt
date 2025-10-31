package io.github.diskria.projektor.minecraft.loaders

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.common.minecraft.versions.common.*
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.helpers.AccessWidenerHelper
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.RemapShadowJarTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import net.ornithemc.ploceus.api.GameSide
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class AbstractFabric(val isOrnithe: Boolean = false) : ModLoader() {

    override val configFilePath: String
        get() = fileName(getName(), "mod", Constants.File.Extension.JSON)

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        val minecraftVersion = mod.minSupportedVersion
        val areSplitMixins = minecraftVersion.areSplitMixins()

        subprojects {
            val side = name.toEnumOrNull<ModSide>() ?: return@subprojects
            val sideProject = this@subprojects

            loom {
                if (areSplitMixins) {
                    when (side) {
                        ModSide.CLIENT -> clientOnlyMinecraftJar()
                        ModSide.SERVER -> serverOnlyMinecraftJar()
                    }
                }
                runs {
                    named(side.getName()) {
                        name = side.getName(`Title Case`)
                        runDir = MinecraftMod.RUN_DIRECTORY_NAME
                        when (side) {
                            ModSide.CLIENT -> client()
                            ModSide.SERVER -> server()
                        }
                        val memoryRange = when (side) {
                            ModSide.CLIENT -> 2..4
                            ModSide.SERVER -> 4..8
                        }
                        vmArgs(
                            *JvmArguments.memory(memoryRange, Size.GIGABYTES),
                        )
                        if (side == ModSide.CLIENT) {
                            programArgs(
                                *JvmArguments.program("username", mod.repo.owner.developer + "_test"),
                                *JvmArguments.program("userProperties", "{}"),
                            )
                        }
                    }
                }
                val resourcesDirectory = projectDir.resolve("src/main/resources")
                val accessWidenerFile = resourcesDirectory.resolve(mod.getAccessWidenerFileName())
                if (!accessWidenerFile.exists()) {
                    accessWidenerFile.ensureFileExists().writeText(AccessWidenerHelper.HEADER)
                }
                accessWidenerPath.set(accessWidenerFile)
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
                named<JavaExec>("run" + side.getName(PascalCase)) {
                    val shadowJar = modProject.tasks.shadowJar
                    dependsOn(shadowJar)
                    classpath += files(shadowJar.get().archiveFile)

                    javaLauncher = sideProject.extensions.getByType<JavaToolchainService>().launcherFor {
                        languageVersion = JavaLanguageVersion.of(minecraftVersion.getMinJavaVersion())
                    }
                }
                processResources {
                    exclude(mod.getAccessWidenerFileName())
                }
            }
            dependencies {
                minecraft("com.mojang", "minecraft", minecraftVersion.asString())
                modImplementation("net.fabricmc", "fabric-loader", Versions.FABRIC_LOADER)

                if (isOrnithe) {
                    ploceus {
                        if (areSplitMixins) {
                            @Suppress("DEPRECATION")
                            when (side) {
                                ModSide.CLIENT -> clientOnlyMappings()
                                ModSide.SERVER -> serverOnlyMappings()
                            }
                        }
                        mappings(featherMappings(minecraftVersion.getOrnitheFeatherMappingsVersion(sideProject)))
                        dependOsl(
                            "0.16.3",
                            if (areSplitMixins) {
                                when (side) {
                                    ModSide.CLIENT -> GameSide.CLIENT
                                    ModSide.SERVER -> GameSide.SERVER
                                }
                            } else {
                                GameSide.MERGED
                            }
                        )
                    }
                } else {
                    mappings(
                        "net.fabricmc",
                        "yarn",
                        minecraftVersion.getFabricYarnMappingsVersion(modProject),
                        "v2"
                    )
                    modImplementation(
                        "net.fabricmc.fabric-api",
                        "fabric-api",
                        minecraftVersion.getFabricApiVersion(modProject)
                    )
                    modImplementation(
                        "net.fabricmc",
                        "fabric-language-kotlin",
                        "${Versions.FABRIC_KOTLIN}+kotlin.${Versions.KOTLIN}"
                    )
                }
            }
            val runDirectory = projectDir.resolve(MinecraftMod.RUN_DIRECTORY_NAME).ensureDirectoryExists()
            if (side == ModSide.SERVER) {
                val eulaName = "eula"
                val eulaFile = runDirectory.resolve(fileName(eulaName, Constants.File.Extension.TXT))
                if (!eulaFile.exists()) {
                    eulaFile.ensureFileExists().writeText("$eulaName=${true}")
                }
                val serverPropertiesFile = runDirectory.resolve(
                    fileName("server", Constants.File.Extension.PROPERTIES)
                )
                if (!serverPropertiesFile.exists()) {
                    serverPropertiesFile.ensureFileExists().writeText("online-mode=${false}")
                }
            }
            registerTask<RemapShadowJarTask> {
                val shadowJarTask = modProject.tasks.shadowJar.get()
                dependsOn(shadowJarTask)
                inputFile.set(shadowJarTask.archiveFile)

                destinationDirectory = modProject.getBuildDirectory("libs")
                archiveBaseName = shadowJarTask.archiveBaseName
                archiveVersion = shadowJarTask.archiveVersion
                archiveClassifier = Constants.Char.EMPTY
            }
        }
        val generateConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(temporaryDir.resolve(configFilePath))
        }
        val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(temporaryDir.resolve(mod.mixinsConfigFileName))
        }
        tasks {
            named("build") {
                dependsOn(children().first().getTask<RemapShadowJarTask>())
            }
            processResources {
                dependsOn(generateConfigTask)
                from(generateConfigTask)

                dependsOn(generateMixinsConfigTask)
                from(generateMixinsConfigTask) {
                    into("assets/${mod.id}")
                }

                from(rootProject.getFile(fileName("icon", Constants.File.Extension.PNG))) {
                    into("assets/${mod.id}")
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
}
