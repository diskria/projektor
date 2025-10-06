package io.github.diskria.projektor.repo.host

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.repo.vcs.VersionControlSystem

sealed class RepoHost(val hostname: String, val versionControlSystem: VersionControlSystem) {

    val sshAuthority: String = versionControlSystem.name + Constants.Char.AT_SIGN + hostname
}

