package builder.metadata_generator

import builder.metadata_generator.extensions.register
import builder.metadata_generator.tasks.GenerateBuildConfigTask
import builder.metadata_generator.tasks.GenerateEnvProviderTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType

class MetadataGeneratorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val generatedDirectory = project.layout.buildDirectory.dir("generated/sources/kotlin/main")
        val generateBuildConfigTask = project.tasks.register<GenerateBuildConfigTask> {
            outputDirectory.set(generatedDirectory.map { it.dir("buildConfig") })
        }
        val generateEnvProviderTask = project.tasks.register<GenerateEnvProviderTask> {
            outputDirectory.set(generatedDirectory.map { it.dir("envProvider") })
        }
        project.extensions.getByType<SourceSetContainer>().named("main").configure { main ->
            main.extensions.configure<SourceDirectorySet>("kotlin") { kotlin ->
                kotlin.srcDirs(generateBuildConfigTask, generateEnvProviderTask)
            }
        }
    }
}
