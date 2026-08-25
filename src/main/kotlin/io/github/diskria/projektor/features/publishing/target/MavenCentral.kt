package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.ensurePluginApplied
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.extensions.registerTask
import io.github.diskria.projektor.extensions.signing
import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.features.generation.readme.shields.live.MavenCentralShield
import io.github.diskria.projektor.features.publishing.tasks.UploadBundleToMavenCentralTask
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider

internal object MavenCentral : MavenPublishingTarget("maven-central") {

    override fun configurePublication(project: Project, projekt: Projekt, publication: MavenPublication) {
        if (!project.providers.isCI) return
        project.ensurePluginApplied("signing")
        project.signing {
            val secrets = SecretsHelper(project.providers)
            useInMemoryPgpKeys(secrets.gpgKey, secrets.gpgPassphrase)
            sign(publication)
        }
    }

    override fun configureDistributeTask(project: Project, projekt: Projekt): TaskProvider<out Task> =
        project.tasks.registerTask<UploadBundleToMavenCentralTask>(SecretsHelper(project.providers)) {
            archiveBaseName.set(projekt.metadata.repo.name)
            archiveVersion.set(projekt.version)
        }

    override fun getHomepage(projekt: Projekt): String =
        "https://central.sonatype.com/artifact/${projekt.metadata.repo.owner.namespace}/${projekt.metadata.repo.name}"

    override fun getReadmeShield(projekt: Projekt): ReadmeShield = MavenCentralShield(projekt)
}
