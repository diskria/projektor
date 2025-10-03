import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.publishing.GitHubPages

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

projekt {
    license = MitLicense
    owner = GithubProfile
    publishingTarget = GitHubPages

    val a = gradlePlugin {
        isSettingsPlugin = true
        tags = setOf("build", "test")
    }
    println(a.packageName)
}
