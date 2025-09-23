package io.github.diskria.projektor.owner

import io.github.diskria.kotlin.utils.extensions.common.buildEmail

object GithubProfile : GithubOwner("diskria") {

    val username: String = name

    override val email: String = buildEmail(username, "proton.me")
}
