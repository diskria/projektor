package io.github.diskria.projektor.internal.git

import org.gradle.api.file.Directory

internal class GitClient private constructor(private val repoDirectory: Directory) {

    fun configureUser(name: String, email: String, isGlobal: Boolean = false) {
        val configArgs = listOfNotNull("config", if (isGlobal) "--global" else null)
        exec(configArgs + listOf("user.name", name))
        exec(configArgs + listOf("user.email", email))
    }

    fun stage(vararg paths: String): Boolean =
        exec(listOf("add") + paths).isSuccess

    fun commit(message: CommitMessage, isAmend: Boolean = false): Boolean {
        val flags = if (isAmend) listOf("commit", "--amend", "-m") else listOf("commit", "-m")
        return exec(flags + message.toString()).isSuccess
    }

    fun push(remoteName: String = ORIGIN_REMOTE_NAME, branchName: String = HEAD): Boolean =
        exec("push", remoteName, branchName).isSuccess

    fun setRemoteUrl(remoteName: String = ORIGIN_REMOTE_NAME, remoteUrl: String) {
        exec("remote", "set-url", remoteName, remoteUrl)
    }

    private fun exec(vararg args: String): ProcessResult = exec(args.toList())

    private fun exec(args: List<String>): ProcessResult {
        val command = listOf("git") + args
        val process = ProcessBuilder(command).directory(repoDirectory.asFile).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText().trim()
        val exitCode = process.waitFor()
        return ProcessResult(exitCode == 0, output)
    }

    private data class ProcessResult(val isSuccess: Boolean, val output: String)

    companion object {
        const val ORIGIN_REMOTE_NAME = "origin"
        const val HEAD = "HEAD"

        fun open(repoDirectory: Directory): GitClient {
            check(repoDirectory.asFile.exists() && repoDirectory.asFile.isDirectory) {
                "Repository directory does not exist or is not a directory: ${repoDirectory.asFile.absolutePath}"
            }
            return GitClient(repoDirectory)
        }
    }
}
