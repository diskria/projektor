package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.extensions.registerTask
import io.github.diskria.projektor.features.distribution.tasks.UploadBundleToMavenCentralTask
import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.features.generation.readme.shields.live.MavenCentralShield
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension

internal object MavenCentralDistributionTarget : MavenDistributionTarget("maven-central") {

    override fun configurePublication(project: Project, projekt: Projekt, publication: MavenPublication) {
        if (!project.providers.isCI) return
        project.pluginManager.apply("signing")
        project.extensions.configure<SigningExtension> {
            val secrets = SecretsHelper(project.providers)
            useInMemoryPgpKeys(secrets.gpgKey, secrets.gpgPassphrase)
            sign(publication)
        }
    }

    override fun configureDistributeTask(project: Project, projekt: Projekt.Regular): TaskProvider<out Task> {
        val publishTask = configurePublishTask(project, projekt)
        return project.tasks.registerTask<UploadBundleToMavenCentralTask>(SecretsHelper(project.providers)) {
            repoName.set(projekt.metadata.repo.name)
            artifactVersion.set(projekt.version)
            from(getLocalMavenDirectory(project))
            destinationDirectory.set(project.layout.buildDirectory.dir("maven-central"))
            dependsOn(publishTask)
        }
    }

    override fun getHomepage(projekt: Projekt): String =
        "https://central.sonatype.com/artifact/${projekt.metadata.repo.owner.namespace}/${projekt.metadata.repo.name}"

    override fun getReadmeShield(projekt: Projekt): ReadmeShield = MavenCentralShield(projekt)
}
