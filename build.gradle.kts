val taskName = "publishAllPublicationsToGitHubPagesRepository"
val repoPath = "build/repo"

tasks.register<Sync>(taskName) {
    group = "publishing"
    childProjects.keys.forEach { projectName ->
        dependsOn(":$projectName:$taskName")
        from("$projectName/$repoPath")
    }
    into(repoPath)
}
