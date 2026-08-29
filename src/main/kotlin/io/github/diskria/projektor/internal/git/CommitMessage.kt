package io.github.diskria.projektor.internal.git

class CommitMessage(val type: CommitType, val subject: String) {
    override fun toString(): String = "${type.name.lowercase()}: $subject"
}

enum class CommitType { TEST, CHORE, BUILD, CI, DOCS }
