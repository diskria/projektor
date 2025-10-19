package io.github.diskria.projektor.publishing.common

import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.tasks.ReleaseTask
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateReadmeTask
import io.github.diskria.projektor.tasks.generate.UpdateGithubRepositoryMetadataTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.withType

abstract class PublishingTarget {

    fun configure(projekt: IProjekt, project: Project) {
        configurePublishing(projekt, project)
        val distribute = configureDistributeTask(project)
        val publish = getPublishTaskName()

        val rootProject = project.rootProject
        rootProject.ensureTaskRegistered<ReleaseTask> {
            val tasks = rootProject.tasks
            val generateLicense = tasks.withType<GenerateLicenseTask>().single()
            val generateReadme = tasks.withType<GenerateReadmeTask>().single()
            val updateGithubRepositoryMetadata = tasks.withType<UpdateGithubRepositoryMetadataTask>().single()

            dependsOn(generateLicense, generateReadme, publish, distribute)
            generateReadme.mustRunAfter(generateLicense)
            tasks.findByName(publish)?.mustRunAfter(generateReadme)
            distribute.mustRunAfter(publish)

            finalizedBy(updateGithubRepositoryMetadata)
        }
    }

    abstract fun configurePublishing(projekt: IProjekt, project: Project)

    abstract fun configureDistributeTask(project: Project): Task

    abstract fun getPublishTaskName(): String

    abstract fun getHomepage(metadata: ProjektMetadata): String

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null
}
