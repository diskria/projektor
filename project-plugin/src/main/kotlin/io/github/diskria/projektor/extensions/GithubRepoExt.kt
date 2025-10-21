package io.github.diskria.projektor.extensions

import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.github.GithubRepo
import java.io.File

fun GithubRepo.pushFiles(repoDirectory: File, commitMessage: String, vararg files: File) {
    with(GitShell.open(repoDirectory)) {
        setRemoteUrl(GitShell.ORIGIN_REMOTE_NAME, getUrl(isVcs = true, token = Secrets.githubToken))
        configureUser(owner.name, owner.email)
        stage(*files.map { it.relativeTo(repoDirectory).path }.toTypedArray())
        commit(commitMessage)
        push()
    }
}
