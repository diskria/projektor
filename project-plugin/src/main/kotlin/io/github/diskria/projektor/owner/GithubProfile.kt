package io.github.diskria.projektor.owner

import io.github.diskria.utils.kotlin.extensions.common.buildEmail

object GithubProfile : GithubOwner("diskria") {

    val username: String = name

    override val email: String = buildEmail(username, "proton.me")
}
