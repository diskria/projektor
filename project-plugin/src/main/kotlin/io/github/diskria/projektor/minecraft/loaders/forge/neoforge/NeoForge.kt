package io.github.diskria.projektor.minecraft.loaders.forge.neoforge

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.extensions.processResources
import io.github.diskria.gradle.utils.extensions.registerTask
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.extensions.neoforge
import io.github.diskria.projektor.extensions.sourceSets
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.slf4j.event.Level

data object NeoForge : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        subprojects {
            val side = name.toEnumOrNull<ModSide>() ?: return@subprojects
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
                    configureEach {
                        systemProperties.put(
                            "forge.logging.markers", "REGISTRIES"
                        )
                        logLevel.set(Level.DEBUG)
                    }
                }
                mods {
                    create(mod.id) {
                        sourceSet(sourceSets["main"])
                    }
                }
            }
            val resourcesDirectory = projectDir.resolve("src/main/resources/META-INF")
            val accessTransformerFile = resourcesDirectory.resolve(mod.getAccessTransformerFileName())
            if (!accessTransformerFile.exists()) {
                accessTransformerFile.ensureFileExists()
            }
        }
        val generateConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(
                temporaryDir.resolve("META-INF/" + fileName(getLoaderName(), "mods", Constants.File.Extension.TOML))
            )
        }
        tasks {
//            named("build") {
//                dependsOn(children().first().getTask<RemapShadowJarTask>())
//            }
            processResources {
                dependsOn(generateConfigTask)
                from(generateConfigTask)

                from(rootProject.getFile(fileName("icon", Constants.File.Extension.PNG))) {
                    into("assets/${mod.id}")
                }
            }
        }
    }
}
