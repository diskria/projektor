package builder.envs_generator

import builder.envs_generator.extensions.register
import builder.envs_generator.models.ContextEnv
import builder.envs_generator.models.SecretEnv
import builder.envs_generator.tasks.GenerateEnvsClassTask
import builder.envs_generator.tasks.GenerateReusableWorkflowTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType

class EnvsGeneratorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register<GenerateReusableWorkflowTask> {
            envs.set(allEnvs)
            taskNames.set(listOf("clean", "releaseProjekt"))
        }
        val generateEnvsClassTask = project.tasks.register<GenerateEnvsClassTask> {
            envs.set(allEnvs)
        }
        val mainSourceSet = project.extensions.getByType<SourceSetContainer>()["main"]
        val kotlinSourceDirectorySet = mainSourceSet.extensions.getByName<SourceDirectorySet>("kotlin")
        kotlinSourceDirectorySet.srcDir(generateEnvsClassTask.map { it.outputDirectory })
    }

    companion object {
        private val contextEnvs = listOf(
            ContextEnv("GITHUB_OWNER", "github.repository_owner"),
            ContextEnv("GITHUB_REPO", "github.event.repository.name"),
        )

        private val secretEnvs = listOf(
            "GITHUB_TOKEN",
            "GITHUB_PACKAGES_TOKEN",
            "GPG_KEY",
            "GPG_PASSPHRASE",
            "SONATYPE_USERNAME",
            "SONATYPE_PASSWORD",
            "GRADLE_PUBLISH_KEY",
            "GRADLE_PUBLISH_SECRET",
        ).map { SecretEnv(it) }

        private val allEnvs = contextEnvs + secretEnvs
    }
}
