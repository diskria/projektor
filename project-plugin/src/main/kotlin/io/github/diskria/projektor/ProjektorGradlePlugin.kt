package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToProjekt
import io.github.diskria.projektor.projekt.metadata.LicenseMetadata
import io.github.diskria.projektor.projekt.metadata.ReadmeMetadata
import io.github.diskria.projektor.projekt.metadata.RepositoryMetadata
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateReadmeTask
import io.github.diskria.projektor.tasks.generate.GenerateRepositoryMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val rootProject = project.rootProject
        val projektMetadata: ProjektMetadata by rootProject.extra.properties

        val extension = project.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            val projekt = configurator.configure(project, projektMetadata.mapToProjekt())
            if (!rootProject.hasTask<GenerateLicenseTask>()) {
                rootProject.registerTask<GenerateLicenseTask> {
                    licenseMetadata.set(LicenseMetadata.of(projekt))
                    licenseFile.set(rootProject.getFile(GenerateLicenseTask.FILE_NAME))
                }
            }
            if (!rootProject.hasTask<GenerateReadmeTask>()) {
                rootProject.registerTask<GenerateReadmeTask> {
                    readmeMetadata.set(ReadmeMetadata.of(projekt))
                    readmeFile.set(rootProject.getFile(GenerateReadmeTask.FILE_NAME))
                    aboutFile.set(rootProject.getFile(GenerateReadmeTask.ABOUT_FILE_NAME))
                }
            }
            if (!rootProject.hasTask<GenerateRepositoryMetadataTask>()) {
                rootProject.registerTask<GenerateRepositoryMetadataTask> {
                    repositoryMetadata.set(RepositoryMetadata.of(projekt))
                    val metadataDirectory = rootProject.getBuildDirectory("metadata").get().asFile
                    outputFile.set(metadataDirectory.resolve(GenerateRepositoryMetadataTask.FILE_NAME))
                }
            }
        }
    }
}
