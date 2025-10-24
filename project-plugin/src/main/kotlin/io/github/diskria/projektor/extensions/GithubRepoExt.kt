package io.github.diskria.projektor.extensions

import io.github.diskria.kotlin.shell.dsl.git.GitShell
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitMessage
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.repo.github.GithubRepo
import java.io.File

fun GithubRepo.pushFiles(repoDirectory: File, commitMessage: CommitMessage, vararg files: File) {
    with(GitShell.open(repoDirectory)) {
        setRemoteUrl(GitShell.ORIGIN_REMOTE_NAME, getUrl(isVcs = true, token = Secrets.githubToken))
        configureUser(owner.developer, owner.email)
        stage(*files.map { it.relativeTo(repoDirectory).path }.toTypedArray())
        commit(commitMessage)
        push()
    }
}
