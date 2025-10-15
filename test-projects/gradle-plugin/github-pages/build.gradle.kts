import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.projektor.publishing.maven.GithubPages
import io.github.diskria.projektor.publishing.maven.LocalMaven

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor) apply false
}

val taskName = GithubPages.getConfigurePublicationTaskName()
tasks.register<Sync>(taskName) {
    childProjects.forEach { (projectName, project) ->
        dependsOn(":$projectName:$taskName")
        from(project.getBuildDirectory(LocalMaven.DIRECTORY_NAME))
    }
    into(getBuildDirectory(LocalMaven.DIRECTORY_NAME))
}
