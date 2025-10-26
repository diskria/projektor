package io.github.diskria.projektor.extensions

import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.shell.dsl.git.GitShell
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitMessage
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.repo.github.GithubRepo
import java.io.File

fun GithubRepo.pushFile(repoDirectory: File, commitType: CommitType, file: File, wasFileExists: Boolean) {
    val action = if (wasFileExists) "update" else "add"
    pushFile(repoDirectory, CommitMessage(commitType, "$action ${file.name}"), file)
}

fun GithubRepo.pushFile(repoDirectory: File, commitMessage: CommitMessage, file: File) {
    with(GitShell.open(repoDirectory)) {
        stage(file.relativeTo(repoDirectory).path)
        if (EnvironmentHelper.isCI()) {
            configureUser(owner.developer, owner.email)
            commit(commitMessage)

            setRemoteUrl(GitShell.ORIGIN_REMOTE_NAME, getUrl(isVcs = true, token = Secrets.githubToken))
            push()
        }
    }
}
