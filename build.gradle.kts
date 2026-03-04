plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

val pluginProjects = listOf(
    project("settings-plugin"),
    project("project-plugin")
)
tasks.register<Sync>("releaseProjekt") {
    pluginProjects.forEach { project ->
        dependsOn(":${project.name}:publishAllPublicationsToGithubPagesRepository")
        from(project.layout.buildDirectory.dir("maven"))
    }
    into(layout.projectDirectory.dir("docs"))
}
