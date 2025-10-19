import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.projektor.publishing.maven.GithubPages
import io.github.diskria.projektor.publishing.maven.common.LocalMavenBasedPublishingTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor) apply false
}

val taskName = GithubPages.getPublishTaskName()
tasks.register<Sync>(taskName) {
    childProjects.forEach { (projectName, project) ->
        dependsOn(":$projectName:$taskName")
        from(project.getBuildDirectory(LocalMavenBasedPublishingTarget.LOCAL_MAVEN_DIRECTORY_NAME))
    }
    into(getBuildDirectory(LocalMavenBasedPublishingTarget.LOCAL_MAVEN_DIRECTORY_NAME))
}

tasks.register<Task>("publishPlugins") {
    childProjects.forEach { (projectName, project) ->
        dependsOn(":$projectName:publishPlugins")
    }
}
