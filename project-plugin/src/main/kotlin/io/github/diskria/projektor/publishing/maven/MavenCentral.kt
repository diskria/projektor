package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.ensureTaskRegistered
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.signing
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.maven.common.MavenPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.live.MavenCentralShield
import io.github.diskria.projektor.tasks.distribute.UploadBundleToMavenCentralTask
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

object MavenCentral : MavenPublishingTarget() {

    override val shouldCreatePublication: Boolean = true

    override fun configurePublication(
        projekt: Projekt,
        project: Project,
        publication: MavenPublication,
    ) = with(project) {
        val componentName = projekt.publicationComponentName ?: return
        with(publication) {
            from(components[componentName])
            pom {
                name.set(projekt.name)
                description.set(projekt.description)
                url.set(projekt.repo.getUrl())
                licenses {
                    license {
                        projekt.license.let { license ->
                            name.set(license.id)
                            url.set(license.url.toString())
                        }
                    }
                }
                developers {
                    developer {
                        projekt.repo.owner.developer.let { developer ->
                            id.set(developer)
                            name.set(developer)
                        }
                        email.set(projekt.repo.owner.email)
                    }
                }
                scm {
                    url.set(projekt.repo.getUrl())
                    connection.set(projekt.repo.getScmConnectionUrl())
                    developerConnection.set(projekt.repo.getScmDeveloperConnectionUrl())
                }
            }
        }
        if (EnvironmentHelper.isCI()) {
            signing {
                useInMemoryPgpKeys(Secrets.gpgKey, Secrets.gpgPassphrase)
                sign(publication)
            }
        }
    }

    override fun getHomepage(metadata: ProjektMetadata): Url =
        buildUrl("central.sonatype.com") {
            path("artifact", metadata.repo.owner.namespace, metadata.repo.name)
        }

    override fun configureDistributeTask(rootProject: Project): Task =
        rootProject.ensureTaskRegistered<UploadBundleToMavenCentralTask>()

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        MavenCentralShield(metadata)
}
