package io.github.diskria.projektor.common.repo.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.common.repo.RepoHost
import kotlinx.serialization.Serializable

@Serializable
data class GithubOwner(val name: String, val email: String) {

    val type: GithubOwnerType
        get() = when {
            name.first().isUpperCase() -> GithubOwnerType.BRAND
            name.contains(Constants.Char.HYPHEN) -> GithubOwnerType.DOMAIN
            else -> GithubOwnerType.PROFILE
        }

    val developer: String
        get() = name.lowercase().modifyIf(type == GithubOwnerType.DOMAIN) { it.substringBefore(Constants.Char.HYPHEN) }

    val namespace: String
        get() = "io".appendPackageName(RepoHost.GITHUB.shortName).appendPackageName(developer)
}
