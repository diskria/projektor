package io.github.diskria.projektor.configurators

import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.extensions.mappers.toEnum
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.minecraft.ModLoaderType
import io.github.diskria.projektor.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : ProjectConfigurator<MinecraftMod>() {

    override fun configureProject(project: Project, projekt: IProjekt): MinecraftMod = with(project) {
        val loader = projectDir.parentFile.name.toEnum<ModLoaderType>().mapToModel()
        val minecraftVersion = MinecraftVersion.of(projectDir.name)
        val minecraftMod = MinecraftMod(projekt, config, loader, minecraftVersion)
        tasks.named<Jar>("jar") {
            manifest {
                val developerName = minecraftMod.metadata.repository.owner.developerName

                val specificationVersion by 1.toString().autoNamedProperty(`Train-Case`)
                val specificationTitle by minecraftMod.id.autoNamedProperty(`Train-Case`)
                val specificationVendor by developerName.autoNamedProperty(`Train-Case`)

                val implementationVersion by minecraftMod.archiveVersion.autoNamedProperty(`Train-Case`)
                val implementationTitle by name.autoNamedProperty(`Train-Case`)
                val implementationVendor by developerName.autoNamedProperty(`Train-Case`)

                attributes(
                    listOf(
                        specificationVersion,
                        specificationTitle,
                        specificationVendor,

                        implementationVersion,
                        implementationTitle,
                        implementationVendor,
                    ).associate { it.name to it.value }
                )
            }
        }
        minecraftMod.loader.configureMod(project, minecraftMod)
        return minecraftMod
    }
}
