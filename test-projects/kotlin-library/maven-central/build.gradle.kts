import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.projektor.publishing.maven.LocalMaven
import io.github.diskria.projektor.publishing.maven.MavenCentral

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor) apply false
}

val taskName = MavenCentral.getConfigurePublicationTaskName()
tasks.register<Sync>(taskName) {
    childProjects.forEach { (projectName, project) ->
        dependsOn(":$projectName:$taskName")
        from(project.getBuildDirectory(LocalMaven.DIRECTORY_NAME))
    }
    into(getBuildDirectory(LocalMaven.DIRECTORY_NAME))
}
