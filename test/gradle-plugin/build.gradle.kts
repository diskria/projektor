import io.github.diskria.projektor.publishing.maven.GithubPages

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.projektor) apply false
}

val taskName = GithubPages.getPublishTaskName()
tasks.register<Sync>(taskName) {
    childProjects.forEach { (projectName, project) ->
        dependsOn(":$projectName:$taskName")
        from(GithubPages.getLocalMavenDirectory(project))
    }
    into(GithubPages.getLocalMavenDirectory(project))
}
