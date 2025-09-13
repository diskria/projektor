package io.github.diskria.projektor.owner.domain

import io.github.diskria.projektor.owner.GithubOwner
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.utils.kotlin.Constants

open class GithubDomain(val suffix: String) : GithubOwner(GithubProfile.username + Constants.Char.HYPHEN + suffix) {
    override val namespace: String = GithubProfile.namespace
    override val email: String = GithubProfile.email
}
