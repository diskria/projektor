package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.common.minecraft.versions.common.getMinJavaVersion
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : ProjectConfigurator<MinecraftMod>() {

    override fun configureProject(project: Project): MinecraftMod = with(project) {
        val mod = project.toProjekt().toMinecraftMod(project, config)
        mod.loader.configure(project, mod)
        project.tasks.named<JavaExec>("runClient") {
            javaLauncher.set(
                project.extensions.getByType<JavaToolchainService>().launcherFor {
                    languageVersion.set(JavaLanguageVersion.of(mod.minSupportedVersion.getMinJavaVersion()))
                }
            )
        }
        return mod
    }
}
