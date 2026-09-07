package io.github.diskria.projektor.core.model.github

import io.github.diskria.projektor.internal.git.CommitMessage
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.git.GitClient
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFile
import java.io.Serializable as PropertySerializable

class GithubRepo(val owner: GithubOwner, val name: String) : PropertySerializable {

    val url: String get() = getUrl()
    val packagesUrl: String get() = "$url/packages"
    val issuesUrl: String get() = "$url/issues"
    val actionsUrl: String get() = "$url/actions"
    val pagesUrl: String get() = "https://${owner.developer}.github.io/$name"

    val path: String get() = "${owner.name}/$name"
    val packagesMavenUrl: String get() = "https://maven.pkg.$host/$path"
    val vcsUrl: String get() = getUrl(vcs = true)
    val scmUrl: String get() = "scm:git:$vcsUrl"
    val scmDeveloperUrl: String get() = "scm:git:git@$host:$path.git"

    private val host: String get() = "github.com"

    fun pushFileOrDirectory(
        repoDirectory: Directory,
        commitMessage: CommitMessage,
        location: FileSystemLocation,
        githubToken: String,
    ) {
        with(GitClient.open(repoDirectory)) {
            stage(location.asFile.relativeTo(repoDirectory.asFile).path)
            configureUser(owner.developer, owner.email)
            commit(commitMessage)
            setRemoteUrl(GitClient.ORIGIN_REMOTE_NAME, getUrl(vcs = true, token = githubToken))
            push()
        }
    }

    fun pushFile(
        repoDirectory: Directory,
        commitType: CommitType,
        location: RegularFile,
        isUpdate: Boolean,
        githubToken: String,
    ) {
        val commitMessage = CommitMessage(commitType, "${if (isUpdate) "update" else "add"} ${location.asFile.name}")
        pushFileOrDirectory(repoDirectory, commitMessage, location, githubToken)
    }

    private fun getUrl(vcs: Boolean = false, token: String? = null): String {
        val repoPath = if (vcs) "$name.git" else name
        val auth = if (token != null) "x-access-token:$token@" else ""
        return "https://${auth}$host/${owner.name}/$repoPath"
    }
}
