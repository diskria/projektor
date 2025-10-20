package io.github.diskria.projektor.configurators

import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : ProjectConfigurator<MinecraftMod>() {

    override fun configureProject(project: Project): MinecraftMod = with(project) {
        val minecraftMod = project.toProjekt().toMinecraftMod(project, config)
        tasks.named<Jar>("jar") {
            manifest {
                val developer = minecraftMod.repo.owner.developer

                val specificationVersion by 1.toString().autoNamedProperty(`Train-Case`)
                val specificationTitle by minecraftMod.id.autoNamedProperty(`Train-Case`)
                val specificationVendor by developer.autoNamedProperty(`Train-Case`)

                val implementationVersion by minecraftMod.archiveVersion.autoNamedProperty(`Train-Case`)
                val implementationTitle by name.autoNamedProperty(`Train-Case`)
                val implementationVendor by developer.autoNamedProperty(`Train-Case`)

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
        minecraftMod.loader.configure(project, minecraftMod)
        return minecraftMod
    }
}
