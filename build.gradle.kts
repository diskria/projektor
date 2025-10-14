import io.github.diskria.gradle.utils.extensions.getBuildDirectory

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

val taskName = "publishAllPublicationsToGithubPagesRepository"
tasks.register<Sync>(taskName) {
    group = "publishing"
    childProjects.forEach { (projectName, project) ->
        dependsOn(":$projectName:$taskName")
        from(project.getBuildDirectory("localMaven"))
    }
    into(getBuildDirectory("localMaven"))
}
