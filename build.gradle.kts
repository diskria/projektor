val taskName = "publishAllPublicationsToLocalMavenRepository"
val repoPath = "build/localMaven"

tasks.register<Sync>(taskName) {
    group = "publishing"
    childProjects.keys.forEach { projectName ->
        dependsOn(":$projectName:$taskName")
        from("$projectName/$repoPath")
    }
    into(repoPath)
}
