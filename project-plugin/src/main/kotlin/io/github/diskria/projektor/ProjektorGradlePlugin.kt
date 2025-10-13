package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.projekt.metadata.GithubMetadata
import io.github.diskria.projektor.projekt.metadata.PublishingMetadata
import io.github.diskria.projektor.projekt.metadata.ReadmeMetadata
import io.github.diskria.projektor.readme.MarkdownHelper
import io.github.diskria.projektor.tasks.GenerateGithubMetadataTask
import io.github.diskria.projektor.tasks.GenerateLicenseTask
import io.github.diskria.projektor.tasks.GeneratePublishingMetadataTask
import io.github.diskria.projektor.tasks.GenerateReadmeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.withType

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val rootProject = project.rootProject
        val projektMetadata: ProjektMetadata by rootProject.extra.properties

        val extension = project.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            val configuredProjekt = configurator.configure(project, extension.buildProjekt(projektMetadata))
            val projekt by configuredProjekt.autoNamedProperty()
            rootProject.extra[projekt.name] = projekt.value
        }
        project.tasks.withType<GenerateLicenseTask>().isEmpty()
        if (project.isRootProject()) {
            project.registerTask<GenerateLicenseTask> {
                metadata.set(projektMetadata)
                licenseFile.set(project.getFile(GenerateLicenseTask.FILE_NAME))
            }
            project.registerTask<GenerateReadmeTask> {
                val projekt: IProjekt by rootProject.extra.properties
                metadata.set(ReadmeMetadata.of(projekt))
                aboutFile.set(project.getFile(MarkdownHelper.fileName("ABOUT")))
                readmeFile.set(project.getFile(MarkdownHelper.fileName("README")))
            }

            val metadataDirectory = project.getBuildDirectory("metadata").get().asFile
            project.registerTask<GenerateGithubMetadataTask> {
                val projekt: IProjekt by rootProject.extra.properties
                metadata.set(GithubMetadata.of(projekt))
                outputFile.set(metadataDirectory.resolve(fileName("github", Constants.File.Extension.JSON)))
            }
            project.registerTask<GeneratePublishingMetadataTask> {
                val projekt: IProjekt by rootProject.extra.properties
                metadata.set(PublishingMetadata.of(projekt))
                outputFile.set(metadataDirectory.resolve(fileName("publishing", Constants.File.Extension.JSON)))
            }
        }
    }
}
