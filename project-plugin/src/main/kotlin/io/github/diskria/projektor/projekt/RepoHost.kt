package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants

sealed class RepoHost(val hostname: String, val vcs: Vcs) {

    val sshAuthority: String = vcs.name + Constants.Char.AT_SIGN + hostname
}

data object GitHub : RepoHost("github.com", Git)
