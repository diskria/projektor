package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.register
import io.github.diskria.projektor.features.distribution.tasks.UploadBundleToMavenCentralTask
import io.github.diskria.projektor.features.generation.readme.MavenCentralShield
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import io.github.diskria.projektor.generated.EnvProvider
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension

internal object MavenCentralDistributionTarget : MavenDistributionTarget(DistributionTargetType.MAVEN_CENTRAL) {

    override fun configurePublication(project: Project, projekt: Projekt, publication: MavenPublication) {
        val env = EnvProvider(project.providers)
        if (!env.isCI) return
        project.pluginManager.apply("signing")
        project.extensions.configure<SigningExtension> {
            useInMemoryPgpKeys(env.gpgKey, env.gpgPassphrase)
            sign(publication)
        }
    }

    override fun configureDistributeTask(project: Project, projekt: Projekt.Distributable): TaskProvider<out Task> {
        val publishTask = configurePublishTask(project, projekt)
        return project.tasks.register<UploadBundleToMavenCentralTask>(EnvProvider(project.providers)) {
            bundleName.set(projekt.name)
            bundleVersion.set(projekt.version)
            dependsOn(publishTask)
        }
    }

    override fun getHomepage(projekt: Projekt.Distributable): String =
        "https://central.sonatype.com/artifact/${projekt.metadata.namespace}/${projekt.name}"

    override fun getReadmeShield(projekt: Projekt.Distributable): ReadmeShield = MavenCentralShield(projekt)
}
