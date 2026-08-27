package io.github.diskria.projektor.core.model.github

import io.github.diskria.projektor.internal.git.CommitMessage
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.git.GitClient
import java.io.File
import java.io.Serializable

internal class GithubRepo(val owner: GithubOwner, val name: String) : Serializable {

    val url: String get() = getUrl()
    val packagesUrl: String get() = "$url/packages"
    val issuesUrl: String get() = "$url/issues"
    val actionsUrl: String get() = "$url/actions"
    val pagesUrl: String get() = "https://${owner.developer}.github.io/$name"

    val packagesMavenUrl: String get() = "https://maven.pkg.$host/${owner.name}/$name"
    val vcsUrl: String get() = getUrl(vcs = true)
    val scmUrl: String get() = "scm:git:$vcsUrl"
    val scmDeveloperUrl: String get() = "scm:git:git@$host:${owner.name}/$name.git"

    private val host: String get() = "github.com"

    fun pushFile(repoDirectory: File, commitMessage: CommitMessage, file: File, githubToken: String) {
        with(GitClient.open(repoDirectory)) {
            stage(file.relativeTo(repoDirectory).path)
            configureUser(owner.developer, owner.email)
            commit(commitMessage)
            setRemoteUrl(GitClient.ORIGIN_REMOTE_NAME, getUrl(vcs = true, token = githubToken))
            push()
        }
    }

    fun pushFile(repoDirectory: File, commitType: CommitType, file: File, wasFileExists: Boolean, githubToken: String) {
        val action = if (wasFileExists) "update" else "add"
        pushFile(repoDirectory, CommitMessage(commitType, "$action ${file.name}"), file, githubToken)
    }

    private fun getUrl(vcs: Boolean = false, token: String? = null): String {
        val repoPath = if (vcs) "$name.git" else name
        val auth = if (token != null) "x-access-token:$token@" else ""
        return "https://${auth}$host/${owner.name}/$repoPath"
    }
}
