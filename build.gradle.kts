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

// TODO remove after 2.3.0 release
subprojects {
    rootProject.extensions.extraProperties.properties.forEach { (key, value) ->
        if (key.startsWith("projekt") && !extensions.extraProperties.has(key)) {
            extensions.extraProperties.set(key, value)
        }
    }
}
