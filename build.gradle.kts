tasks.register<Sync>("publishAllToGithubPagesMaven") {
    group = "publishing"
    description = "Merges all plugin repositories into one for GitHub Pages"

    val plugins = listOf("project-plugin", "settings-plugin")

    dependsOn(plugins.map { ":$it:publishAllPublicationsToGithubPagesRepository" })
    from(plugins.map { "$it/build/repo" })

    into("build/repo")
}
