package io.github.diskria.projektor.owner.brand

import io.github.diskria.projektor.owner.GithubOwner
import io.github.diskria.projektor.owner.GithubProfile

open class GithubBrand(name: String) : GithubOwner(name) {
    override val namespace: String = GithubProfile.namespace
    override val email: String = GithubProfile.email
}
