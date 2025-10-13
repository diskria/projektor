plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

val taskName = "publishAllPublicationsToGithubPagesRepository"
val repoPath = "build/localMaven"

tasks.register<Sync>(taskName) {
    group = "publishing"
    childProjects.keys.forEach { projectName ->
        dependsOn(":$projectName:$taskName")
        from("$projectName/$repoPath")
    }
    into(repoPath)
}
