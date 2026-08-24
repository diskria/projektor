package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
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
        with(publication) {
            val repoUrl = projekt.repo.getUrl()
            pom.name.set(projekt.name)
            pom.description.set(projekt.description)
            pom.url.set(repoUrl)
            pom.licenses { spec ->
                spec.license { license ->
                    license.name.set(projekt.license.id)
                    license.url.set(projekt.license.url)
                }
            }
            pom.developers { spec ->
                spec.developer {
                    with(it) {
                        id.set(projekt.repo.owner.developer)
                        name.set(projekt.repo.owner.developer)
                        email.set(projekt.repo.owner.email)
                    }
                }
            }
            pom.scm {
                it.url.set(repoUrl)
                it.connection.set(projekt.repo.getScmConnectionUrl())
                it.developerConnection.set(projekt.repo.getScmDeveloperConnectionUrl())
            }
        }
        with(project) {
            if (project.providers.isCI) {
                ensurePluginApplied("signing")
                signing {
                    val secrets = SecretsHelper(project.providers)
                    useInMemoryPgpKeys(secrets.gpgKey, secrets.gpgPassphrase)
                    sign(publication)
                }
            }
        }
    }

    override fun configureDistributeTask(project: Project): TaskProvider<out Task> =
        project.tasks.registerTask<UploadBundleToMavenCentralTask>(SecretsHelper(project.providers))

    override fun getHomepage(metadata: ProjektMetadata): String =
        "https://central.sonatype.com/artifact/${metadata.repo.owner.namespace}/${metadata.repo.name}"

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        MavenCentralShield(metadata)
}
