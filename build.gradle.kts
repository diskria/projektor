val taskName = "publishAllPublicationsToGitHubPagesRepository"
val repoPath = "build/repo"

tasks.register<Sync>(taskName) {
    group = "publishing"
    description = "Merges all plugin repositories into one for GitHub Pages"
    childProjects.keys.forEach { projectName ->
        dependsOn(":$projectName:$taskName")
        from("$projectName/$repoPath")
    }
    into(repoPath)
}
