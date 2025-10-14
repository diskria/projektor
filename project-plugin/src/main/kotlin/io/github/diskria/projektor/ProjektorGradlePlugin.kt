package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.projekt.metadata.GithubMetadata
import io.github.diskria.projektor.projekt.metadata.ReadmeMetadata
import io.github.diskria.projektor.tasks.generate.GenerateGithubMetadataTask
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateReadmeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val rootProject = project.rootProject
        val projektMetadata: ProjektMetadata by rootProject.extra.properties

        val extension = project.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            val configuredProjekt = configurator.configure(project, extension.buildProjekt(projektMetadata))
            val projekt by configuredProjekt.autoNamedProperty()
            rootProject.extra.put(projekt)
        }
        if (!rootProject.hasTask<GenerateLicenseTask>()) {
            rootProject.registerTask<GenerateLicenseTask> {
                metadata.set(projektMetadata)
                licenseFile.set(project.getFile(GenerateLicenseTask.FILE_NAME))
            }
        }
        if (!rootProject.hasTask<GenerateReadmeTask>()) {
            rootProject.registerTask<GenerateReadmeTask> {
                val projekt: IProjekt by rootProject.extra.properties
                metadata.set(ReadmeMetadata.of(projekt))
                aboutFile.set(project.getFile(GenerateReadmeTask.ABOUT_FILE_NAME))
                readmeFile.set(project.getFile(GenerateReadmeTask.FILE_NAME))
            }
        }
        val metadataDirectory = project.getBuildDirectory("metadata").get().asFile
        if (!rootProject.hasTask<GenerateGithubMetadataTask>()) {
            rootProject.registerTask<GenerateGithubMetadataTask> {
                val projekt: IProjekt by rootProject.extra.properties
                metadata.set(GithubMetadata.of(projekt))
                outputFile.set(metadataDirectory.resolve(GenerateGithubMetadataTask.FILE_NAME))
            }
        }
    }
}
