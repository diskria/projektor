package io.github.diskria.projektor.core.model.github

import io.github.diskria.projektor.internal.git.CommitMessage
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.git.GitClient
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
internal data class GithubRepo(val owner: GithubOwner, val name: String) {

    private val ownerName: String get() = owner.name
    private val host: String = "github.com"

    fun getUrl(isVcs: Boolean = false, token: String? = null): String {
        val repoPath = if (isVcs) "$name.git" else name
        val auth = if (token != null) "x-access-token:$token@" else ""
        return "https://${auth}$host/$ownerName/$repoPath"
    }

    fun getPath(isVcs: Boolean = false): String =
        if (isVcs) "$ownerName/$name.git" else "$ownerName/$name"

    fun getIssuesUrl(): String = "https://$host/$ownerName/$name/issues"

    fun getPackagesMavenUrl(): String = "https://maven.pkg.$host/$ownerName/$name"

    fun getPagesUrl(): String = "https://${owner.developer}.github.io/$name"

    fun getScmConnectionUrl(): String = "scm:git:${getUrl(isVcs = true)}"

    fun getScmDeveloperConnectionUrl(): String = "scm:git:git@$host:${getPath(isVcs = true)}"

    fun pushFile(repoDirectory: File, commitMessage: CommitMessage, file: File, githubToken: String) {
        with(GitClient.open(repoDirectory)) {
            stage(file.relativeTo(repoDirectory).path)
            configureUser(owner.developer, owner.email)
            commit(commitMessage)
            setRemoteUrl(GitClient.ORIGIN_REMOTE_NAME, getUrl(isVcs = true, token = githubToken))
            push()
        }
    }

    fun pushFile(repoDirectory: File, commitType: CommitType, file: File, wasFileExists: Boolean, githubToken: String) {
        val action = if (wasFileExists) "update" else "add"
        pushFile(repoDirectory, CommitMessage(commitType, "$action ${file.name}"), file, githubToken)
    }
}
