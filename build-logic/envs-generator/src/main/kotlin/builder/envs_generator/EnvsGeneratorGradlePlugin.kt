package builder.envs_generator

import builder.envs_generator.extensions.register
import builder.envs_generator.tasks.GenerateEnvsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType

class EnvsGeneratorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val generateEnvsTask = project.tasks.register<GenerateEnvsTask> {
            outputDirectory.set(project.layout.buildDirectory.dir("generated/sources/kotlin/main"))
        }
        val mainSourceSet = project.extensions.getByType<SourceSetContainer>()["main"]
        val kotlinSourceDirectorySet = mainSourceSet.extensions.getByName<SourceDirectorySet>("kotlin")
        kotlinSourceDirectorySet.srcDir(generateEnvsTask)
    }
}
