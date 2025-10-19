package io.github.diskria.projektor.tasks

import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateReadmeTask
import io.github.diskria.projektor.tasks.generate.UpdateGithubRepositoryMetadataTask
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.withType

abstract class ReleaseTask : DefaultTask() {

    @get:Internal
    abstract val publishTaskName: Property<String>

    @get:Internal
    abstract val distributeTaskName: Property<String>

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        project.afterEvaluate {
            configurePreRelease(tasks)
            configurePostRelease(tasks)
        }
    }

    private fun configurePreRelease(tasks: TaskContainer) = with(tasks) {
        val generateLicense = withType<GenerateLicenseTask>()
        val generateReadme = withType<GenerateReadmeTask>()
        val publish = named(publishTaskName.get())
        val distribute = named(distributeTaskName.get())

        dependsOn(generateLicense, generateReadme, publish, distribute)

        publish.configure {
            mustRunAfter(generateLicense, generateReadme)
        }
        distribute.configure {
            mustRunAfter(publish)
        }
    }

    private fun configurePostRelease(tasks: TaskContainer) = with(tasks) {
        val updateGithubRepositoryMetadata = withType<UpdateGithubRepositoryMetadataTask>()
        finalizedBy(updateGithubRepositoryMetadata)
    }
}
